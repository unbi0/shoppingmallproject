package elice.shoppingmallproject.global.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import elice.shoppingmallproject.domain.auth.entity.Refresh;
import elice.shoppingmallproject.domain.auth.repository.RefreshRepository;
import elice.shoppingmallproject.domain.user.entity.CustomUserDetails;
import elice.shoppingmallproject.domain.user.entity.Role;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {
    private static final long ACCESS_TOKEN_EXPIRATION_MS = 600000L; // 10 minutes
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
        String email = obtainUsername(request);
        String password = obtainPassword(request);

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, password, null);
        return authenticationManager.authenticate(authToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authentication) throws IOException, ServletException {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        String email = customUserDetails.getUsername();
        Role role = Role.valueOf(authentication.getAuthorities().iterator().next().getAuthority());

        String accessToken = jwtUtil.createJwt("access", email, role, ACCESS_TOKEN_EXPIRATION_MS);
        String refreshToken = jwtUtil.createJwt("refresh", email, role, REFRESH_TOKEN_EXPIRATION_MS);

        addRefreshEntity(email, refreshToken, REFRESH_TOKEN_EXPIRATION_MS);
        response.setHeader(ACCESS_TOKEN_HEADER, accessToken);
        response.addCookie(createCookie(REFRESH_TOKEN_COOKIE_NAME, refreshToken));
        response.setStatus(HttpStatus.OK.value());

        // 응답 본문에 추가 정보로 토큰 반환
        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        new ObjectMapper().writeValue(response.getWriter(), tokens);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    public static Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(COOKIE_MAX_AGE);
        cookie.setHttpOnly(true);
        return cookie;
    }

    private void addRefreshEntity(String email, String refreshToken, Long expiredMs) {
        Date expiration = new Date(System.currentTimeMillis() + expiredMs);

        Refresh refresh = new Refresh();
        refresh.setUsername(email);
        refresh.setRefresh(refreshToken);
        refresh.setExpiration(expiration.toString());

        refreshRepository.save(refresh);
    }
}