package elice.shoppingmallproject.global.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import elice.shoppingmallproject.domain.auth.entity.Refresh;
import elice.shoppingmallproject.domain.auth.repository.RefreshRepository;
import elice.shoppingmallproject.domain.user.dto.UserLoginDto;
import elice.shoppingmallproject.domain.user.entity.CustomUserDetails;
import elice.shoppingmallproject.domain.user.entity.Role;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StreamUtils;

@Slf4j
@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {
    private static final long ACCESS_TOKEN_EXPIRATION_MS = 86400000L; // 10 minutes
    private static final long REFRESH_TOKEN_EXPIRATION_MS = 86400000L; // 24 hours
    private static final String ACCESS_TOKEN_HEADER = "access";
    private static final String REFRESH_TOKEN_COOKIE_NAME = "refresh";
    private static final int COOKIE_MAX_AGE = 24 * 60 * 60; // 24 hours in seconds

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        UserLoginDto userLoginDto = new UserLoginDto();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ServletInputStream inputStream = request.getInputStream();
            String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
            userLoginDto = objectMapper.readValue(messageBody, UserLoginDto.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String email = userLoginDto.getEmail();
        String password = userLoginDto.getPassword();


        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, password, null);
        return authenticationManager.authenticate(authToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authentication) throws IOException, ServletException {
        logger.info("로그인 성공, access, refresh token 발급");
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        Long userId = customUserDetails.getId();
        String email = customUserDetails.getUsername();
        String roleKey = authentication.getAuthorities().iterator().next().getAuthority();
        Role role = Role.fromKey(roleKey);


        String accessToken = jwtUtil.createJwt(userId,"access", email, role, ACCESS_TOKEN_EXPIRATION_MS);
        String refreshToken = Refresh.generateToken();

        Refresh refresh = Refresh.createRefresh(email, refreshToken, REFRESH_TOKEN_EXPIRATION_MS);

        refreshRepository.save(refresh);

        response.addCookie(createCookie(ACCESS_TOKEN_HEADER, accessToken));
        response.addCookie(createCookie(REFRESH_TOKEN_COOKIE_NAME, refresh.getToken()));
        response.setStatus(HttpStatus.OK.value());

    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException, ServletException {
        logger.error("로그인 실패 " + failed.getMessage());
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    public static Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(COOKIE_MAX_AGE);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        return cookie;
    }
}