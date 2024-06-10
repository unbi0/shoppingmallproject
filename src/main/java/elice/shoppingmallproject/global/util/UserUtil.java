package elice.shoppingmallproject.global.util;

import elice.shoppingmallproject.domain.user.entity.CustomUserDetails;
import elice.shoppingmallproject.global.oauth2.userinfo.CustomOAuth2User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class UserUtil {
    public Long getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();

            if (principal instanceof CustomUserDetails userDetails) {
                return userDetails.getId();
            } else if (principal instanceof CustomOAuth2User oAuth2User) {
                return oAuth2User.getUserId();
            }
        }
        return null;
    }
}
