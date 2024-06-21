package elice.shoppingmallproject.domain.auth.service;

import static elice.shoppingmallproject.global.jwt.LoginFilter.createCookie;

import elice.shoppingmallproject.domain.auth.entity.Refresh;
import elice.shoppingmallproject.domain.auth.repository.RefreshRepository;
import elice.shoppingmallproject.domain.user.entity.Role;
import elice.shoppingmallproject.domain.user.entity.User;
import elice.shoppingmallproject.domain.user.repository.UserRepository;
import elice.shoppingmallproject.global.jwt.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ReissueService {
    private static final long ACCESS_TOKEN_EXPIRATION_MS = 8600000L;
    private static final long REFRESH_TOKEN_EXPIRATION_MS = 8600000L;
    private static final String ACCESS_TOKEN_HEADER = "access";
    private static final String REFRESH_TOKEN_HEADER = "refresh";


    private final JwtUtil jwtUtil;
    private final RefreshRepository refreshRepository;
    private final UserRepository userRepository;

    public ResponseEntity<String> reissueToken(HttpServletRequest request, HttpServletResponse response) {

        String refreshToken = findRefreshToken(request);
        if (refreshToken == null) {
            return new ResponseEntity<>("refresh token null", HttpStatus.BAD_REQUEST);
        }

        try {
            validateRefreshToken(refreshToken);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        Refresh existToken = refreshRepository.findByToken(refreshToken);
        String email = existToken.getEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Role role = user.getRole();

        refreshRepository.deleteByToken(refreshToken);

        Refresh newRefreshToken = Refresh.createRefresh(email, Refresh.generateToken(), REFRESH_TOKEN_EXPIRATION_MS);
        refreshRepository.save(newRefreshToken);

        String newAccess = jwtUtil.createJwt(user.getId(), ACCESS_TOKEN_HEADER, email, role,
                ACCESS_TOKEN_EXPIRATION_MS);

        response.addCookie(createCookie(REFRESH_TOKEN_HEADER, newRefreshToken.getToken()));
        response.addCookie(createCookie(ACCESS_TOKEN_HEADER, newAccess));

        return new ResponseEntity<>("Token reissued successfully", HttpStatus.OK);
    }

    private String findRefreshToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(REFRESH_TOKEN_HEADER)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private void validateRefreshToken(String refreshToken) {
        Refresh existToken = refreshRepository.findByToken(refreshToken);
        if (existToken == null) {
            throw new IllegalArgumentException("invalid refresh token");
        }

        if (existToken.getExpiration().before(new Date())) {
            throw new IllegalArgumentException("refresh token expired");
        }
    }
}
