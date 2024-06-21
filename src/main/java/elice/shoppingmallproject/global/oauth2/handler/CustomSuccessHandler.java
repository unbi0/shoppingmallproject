package elice.shoppingmallproject.global.oauth2.handler;

import static elice.shoppingmallproject.global.jwt.LoginFilter.createCookie;

import elice.shoppingmallproject.domain.auth.entity.Refresh;
import elice.shoppingmallproject.domain.auth.repository.RefreshRepository;
import elice.shoppingmallproject.domain.user.entity.Role;
import elice.shoppingmallproject.global.jwt.JwtUtil;
import elice.shoppingmallproject.global.oauth2.userinfo.CustomOAuth2User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private static final long ACCESS_TOKEN_EXPIRATION_MS = 86400000L;
    private static final long REFRESH_TOKEN_EXPIRATION_MS = 8600000L;
    private static final String ACCESS_TOKEN_HEADER = "access";
    private static final String REFRESH_TOKEN_COOKIE_NAME = "refresh";

    private final JwtUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = customUserDetails.getAttributes();

        Long userId = customUserDetails.getUserId();
        String email = (String) attributes.get("email");

        String roleKey = customUserDetails.getAuthorities().stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No authority found"))
                .getAuthority();

        Role role = Role.fromKey(roleKey);

        String accessToken = jwtUtil.createJwt(userId, ACCESS_TOKEN_HEADER, email, role, ACCESS_TOKEN_EXPIRATION_MS);
        String refreshToken = Refresh.generateToken();

        Refresh refresh = Refresh.createRefresh(email, refreshToken, REFRESH_TOKEN_EXPIRATION_MS);
        refreshRepository.save(refresh);

        response.addCookie(createCookie(ACCESS_TOKEN_HEADER, accessToken));
        response.addCookie(createCookie(REFRESH_TOKEN_COOKIE_NAME, refresh.getToken()));

        getRedirectStrategy().sendRedirect(request, response, "/");
    }
}
