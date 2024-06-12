package elice.shoppingmallproject.domain.user.service;

import elice.shoppingmallproject.domain.address.entity.Address;
import elice.shoppingmallproject.domain.user.dto.UserDetail;
import elice.shoppingmallproject.domain.user.dto.UserManagementDto;
import elice.shoppingmallproject.domain.user.dto.UserResponseDto;
import elice.shoppingmallproject.domain.user.dto.UserSignUpDto;
import elice.shoppingmallproject.domain.user.dto.UserUpdateDto;
import elice.shoppingmallproject.domain.user.entity.Role;
import elice.shoppingmallproject.domain.user.entity.User;
import elice.shoppingmallproject.domain.user.exception.DuplicateEmailException;
import elice.shoppingmallproject.domain.user.exception.UserNotFoundException;
import elice.shoppingmallproject.domain.user.repository.UserRepository;
import elice.shoppingmallproject.global.util.UserUtil;
import java.util.List;
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
    private final UserUtil userUtil;

    public void signUp(UserSignUpDto userSignUpDto) {

        if (userRepository.findByEmail(userSignUpDto.getEmail()).isPresent()) {
            throw new DuplicateEmailException("이미 존재하는 이메일입니다.");
        }

        Address address = Address.createAddress(userSignUpDto.getAddress());

        User user = User.createUser(userSignUpDto.getUsername(), userSignUpDto.getEmail(),
                userSignUpDto.getPassword(), address);

        user.passwordEncode(bCryptPasswordEncoder);

        userRepository.save(user);
    }

    // My page 에 들어갈 시 필요한 정보 갖고오는 조회 로직
    public UserResponseDto getUserById() {
        Long loginUserId = userUtil.getAuthenticatedUser();

        User user = userRepository.findById(loginUserId)
                .orElseThrow(() -> new UserNotFoundException("조회된 유저가 없습니다."));

        return new UserResponseDto(user);
    }

    // 시큐리티에서 이미 지금 로그인한 유저가 Admin 인 것을 인증했기때문에 굳이 인증할 필요 없음
    // 관리자 페이지에서 가입 유저를 조회
    public UserManagementDto getUsers() {
        List<User> users = userRepository.findAll();

        int totalUserCount = users.size();
        int adminCount = (int) users.stream()
                .filter(user -> user.getRole() == Role.ADMIN)
                .count();

        List<UserDetail> userDetails = users.stream()
                .map(user -> new UserDetail(user.getCreateAt(), user.getEmail(), user.getUsername(),
                        user.getRole().name()))
                .toList();

        return new UserManagementDto(totalUserCount, adminCount, userDetails);

    }
/*
* 내 정보 수정, 회원 탈퇴같은 경우 어차피 현재 로그인 중인 user 를 갖고와서
* 내 정보를 수정하고, 회원 탈퇴를 하기 때문에
* 별도의 검증을 하는 로직은 필요없나?
* */
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

        userRepository.delete(user);
    }
}
