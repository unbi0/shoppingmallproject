package elice.shoppingmallproject.domain.user.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import elice.shoppingmallproject.domain.address.entity.Address;
import elice.shoppingmallproject.domain.user.dto.UserSignUpDto;
import elice.shoppingmallproject.domain.user.entity.User;
import elice.shoppingmallproject.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

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

        Address address = Address.createAddress(userSignUpDto.getPostcode(), userSignUpDto.getAddress(),
                userSignUpDto.getDetailAddress());

        User user = User.createUser(userSignUpDto.getUsername(), userSignUpDto.getEmail(),
                userSignUpDto.getPassword(), address);

        user.passwordEncode(bCryptPasswordEncoder);

        userRepository.save(user);
    }
}
