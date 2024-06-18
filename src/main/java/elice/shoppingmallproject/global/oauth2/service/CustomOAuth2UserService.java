package elice.shoppingmallproject.global.oauth2.service;

import elice.shoppingmallproject.domain.auth.entity.Auth;
import elice.shoppingmallproject.domain.auth.entity.Provider;
import elice.shoppingmallproject.domain.auth.repository.AuthRepository;
import elice.shoppingmallproject.domain.user.entity.Role;
import elice.shoppingmallproject.domain.user.entity.User;
import elice.shoppingmallproject.domain.user.repository.UserRepository;
import elice.shoppingmallproject.global.oauth2.userinfo.CustomOAuth2User;
import elice.shoppingmallproject.global.oauth2.userinfo.GoogleOAuth2UserInfo;
import elice.shoppingmallproject.global.oauth2.userinfo.NaverOAuth2UserInfo;
import elice.shoppingmallproject.global.oauth2.userinfo.OAuth2UserDto;
import elice.shoppingmallproject.global.oauth2.userinfo.OAuth2UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final AuthRepository authRepository;
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        System.out.println(oAuth2User);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        OAuth2UserInfo oAuth2UserInfo;
        if (registrationId.equals("google")) {
            oAuth2UserInfo = new GoogleOAuth2UserInfo(oAuth2User.getAttributes());
        } else if (registrationId.equals("naver")) {
            oAuth2UserInfo = new NaverOAuth2UserInfo(oAuth2User.getAttributes());
        } else {
            throw new OAuth2AuthenticationException("Unsupported provider: " + registrationId);
        }

        OAuth2UserDto oAuth2UserDto = new OAuth2UserDto(oAuth2UserInfo);

        User user = userRepository.findByEmail(oAuth2UserDto.getEmail())
                .orElseGet(() -> registerNewUser(oAuth2UserDto));

        oAuth2UserDto.setUserId(user.getId());

        Auth auth = authRepository.findByUserAndProvider(user, Provider.valueOf(oAuth2UserDto.getProvider().toUpperCase()))
                .orElseGet(() -> createNewAuth(user, oAuth2UserDto));



        return new CustomOAuth2User(oAuth2UserDto);

    }
    private User registerNewUser(OAuth2UserDto oAuth2UserDto) {
        User user = User.builder()
                .username(oAuth2UserDto.getName())
                .email(oAuth2UserDto.getEmail())
                .password("N/A")
                .role(Role.USER)
                .build();
        return userRepository.save(user);
    }

    private Auth createNewAuth(User user, OAuth2UserDto oAuth2UserDto) {
        Auth auth = Auth.builder()
                .provider(Provider.valueOf(oAuth2UserDto.getProvider().toUpperCase()))
                .providerId(oAuth2UserDto.getProviderId())
                .user(user)
                .build();
        return authRepository.save(auth);
    }

}
