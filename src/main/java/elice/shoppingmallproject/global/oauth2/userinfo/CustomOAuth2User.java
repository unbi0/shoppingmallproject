package elice.shoppingmallproject.global.oauth2.userinfo;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

@RequiredArgsConstructor
public class CustomOAuth2User implements OAuth2User {

    private final OAuth2UserDto oAuth2UserDto;

    @Override
    public Map<String, Object> getAttributes() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("provider", oAuth2UserDto.getProvider());
        attributes.put("providerId", oAuth2UserDto.getProviderId());
        attributes.put("email", oAuth2UserDto.getEmail());
        attributes.put("name", oAuth2UserDto.getName());
        attributes.put("userId", oAuth2UserDto.getUserId());
        return attributes;
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")); // 예시로 ROLE_USER 추가
    }
    @Override
    public String getName() {

        return oAuth2UserDto.getName();
    }
    public Long getUserId() {
        return oAuth2UserDto.getUserId();
    }
}
