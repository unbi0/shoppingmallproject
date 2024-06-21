package elice.shoppingmallproject.domain.user.service;

import elice.shoppingmallproject.domain.address.dto.AddressResponseDto;
import elice.shoppingmallproject.domain.address.entity.Address;
import elice.shoppingmallproject.domain.cart.repository.CartRepository;
import elice.shoppingmallproject.domain.user.dto.UserDetail;
import elice.shoppingmallproject.domain.user.dto.UserManagementDto;
import elice.shoppingmallproject.domain.user.dto.UserResponseDto;
import elice.shoppingmallproject.domain.user.dto.UserSignUpDto;
import elice.shoppingmallproject.domain.user.dto.UserUpdateDto;
import elice.shoppingmallproject.domain.user.entity.Role;
import elice.shoppingmallproject.domain.user.entity.User;
import elice.shoppingmallproject.domain.user.exception.UserNotFoundException;
import elice.shoppingmallproject.domain.user.repository.UserRepository;
import elice.shoppingmallproject.global.util.UserUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserUtil userUtil;

    public void signUp(UserSignUpDto userSignUpDto) {

        if (userRepository.findByEmail(userSignUpDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        Address address = Address.createAddress(userSignUpDto.getAddress());

        User user = User.createUser(userSignUpDto.getUsername(), userSignUpDto.getEmail(),
                userSignUpDto.getPassword(), address, Role.USER);

        user.passwordEncode(bCryptPasswordEncoder);

        userRepository.save(user);
    }

    // My page 에 들어갈 시 필요한 정보 갖고오는 조회 로직
    public UserResponseDto getUserById() {
        Long loginUserId = userUtil.getAuthenticatedUser();

        User user = userRepository.findById(loginUserId)
                .orElseThrow(() -> new UserNotFoundException("조회된 유저가 없습니다."));

        Address address = user.getAddress();
        AddressResponseDto addressResponseDto = (address != null) ? new AddressResponseDto(address) : null;

        return new UserResponseDto(user, addressResponseDto);
    }

    // 시큐리티에서 이미 지금 로그인한 유저가 Admin 인 것을 인증했기때문에 굳이 인증할 필요 없음
    // 관리자 페이지에서 가입 유저를 조회
    public UserManagementDto getUsers(Pageable pageable) {

        Page<User> users = userRepository.findAll(pageable);

        int totalUserCount = (int) users.getTotalElements();
        int adminCount = (int) users.stream()
                .filter(user -> user.getRole() == Role.ADMIN)
                .count();

        List<UserDetail> userDetails = users.stream()
                .map(user -> new UserDetail(user.getCreateAt(), user.getEmail(), user.getUsername(),
                        user.getRole().name()))
                .toList();

        return new UserManagementDto(totalUserCount, adminCount, userDetails);

    }

    // 내 정보 수정
    public void updateUser(UserUpdateDto userUpdateDto) {
        Long loginUserId = userUtil.getAuthenticatedUser();

        User user = userRepository.findById(loginUserId)
                .orElseThrow(() -> new UserNotFoundException("조회된 유저가 없습니다."));

        user.updateUser(userUpdateDto);

        userRepository.save(user);
    }

    // 회원탈퇴
    public void deleteUser() {
        Long loginUserId = userUtil.getAuthenticatedUser();

        User user = userRepository.findById(loginUserId)
                .orElseThrow(() -> new UserNotFoundException("조회된 유저가 없습니다."));
        cartRepository.deleteByUserId(loginUserId);

        userRepository.delete(user);
    }
}
