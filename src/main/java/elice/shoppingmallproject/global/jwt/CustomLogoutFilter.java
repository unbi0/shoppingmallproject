package elice.shoppingmallproject.global.jwt;

import elice.shoppingmallproject.domain.auth.repository.RefreshRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.GenericFilter;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomLogoutFilter extends GenericFilter {

    private static final String LOGOUT_URI = "/logout";
    private static final String LOGOUT_METHOD = "POST";
    private static final String REFRESH_COOKIE_NAME = "refresh";
    private static final String REFRESH_CATEGORY = "refresh";

    private final JwtUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // 로그아웃 경로 검증
        if (!isLogoutRequest(httpRequest)) {
            chain.doFilter(request, response);
            return;
        }

        // refresh token 조회, null 검증
        String refreshToken = getRefreshTokenFromCookies(httpRequest.getCookies());
        if (refreshToken == null) {
            httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // refresh token 만료 시간 체크
        try {
            jwtUtil.isExpired(refreshToken);
        } catch (ExpiredJwtException e) {
            httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // 토큰이 refresh token 인지 확인
        if (!REFRESH_CATEGORY.equals(jwtUtil.getCategory(refreshToken))) {
            httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // DB 에 저장되어 있는지 확인
        if (!refreshRepository.existsByRefresh(refreshToken)) {
            httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // 로그아웃 진행
        // refersh token DB에서 삭제, refresh token cookie 값 -> 0
        refreshRepository.deleteByRefresh(refreshToken);
        clearRefreshTokenCookie(httpResponse);

        httpResponse.setStatus(HttpServletResponse.SC_OK);
    }

    private boolean isLogoutRequest(HttpServletRequest request) {
        return LOGOUT_URI.equals(request.getRequestURI()) && LOGOUT_METHOD.equals(request.getMethod());
    }

    private String getRefreshTokenFromCookies(Cookie[] cookies) {
        if (cookies == null) {
            return null;
        }
        return Arrays.stream(cookies)
                .filter(cookie -> REFRESH_COOKIE_NAME.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

    private void clearRefreshTokenCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(REFRESH_COOKIE_NAME, null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}