package elice.shoppingmallproject.global.oauth2.userinfo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OAuth2UserDto {

    private Long userId;
    private String provider;
    private String providerId;
    private String email;
    private String name;

    public OAuth2UserDto(OAuth2UserInfo oAuth2UserInfo) {
        this.provider = oAuth2UserInfo.getProvider();
        this.providerId = oAuth2UserInfo.getProviderId();
        this.email = oAuth2UserInfo.getEmail();
        this.name = oAuth2UserInfo.getName();
    }
}
