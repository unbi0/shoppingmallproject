package elice.shoppingmallproject.domain.user.service;

import elice.shoppingmallproject.domain.user.dto.UserSignUpDto;
import elice.shoppingmallproject.domain.user.entity.Role;
import elice.shoppingmallproject.domain.user.entity.User;
import elice.shoppingmallproject.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public void signUp(UserSignUpDto userSignUpDto) {

        if (userRepository.findByEmail(userSignUpDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        User user = User.builder()
                .email(userSignUpDto.getEmail())
                .password(userSignUpDto.getPassword())
                .role(Role.USER)
                .build();
        user.passwordEncode(bCryptPasswordEncoder);

        userRepository.save(user);
    }
}
