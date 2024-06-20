package elice.shoppingmallproject.domain.user.controller;

import elice.shoppingmallproject.domain.user.dto.UserManagementDto;
import elice.shoppingmallproject.domain.user.dto.UserResponseDto;
import elice.shoppingmallproject.domain.user.dto.UserSignUpDto;
import elice.shoppingmallproject.domain.user.dto.UserUpdateDto;
import elice.shoppingmallproject.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/sign-up")
    public ResponseEntity<String> signUp(@RequestBody UserSignUpDto userSignUpDto) {
        userService.signUp(userSignUpDto);

        return ResponseEntity.status(HttpStatus.CREATED).body("Created");
    }

    @GetMapping("/my")
    public ResponseEntity<UserResponseDto> getUser() {

        return ResponseEntity.ok(userService.getUserById());
    }

    @GetMapping("/admin/users")
    public ResponseEntity<UserManagementDto> getUsers(@RequestParam(value = "page", defaultValue = "0") int page,
                                                      @RequestParam(value = "size", defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);

        return ResponseEntity.ok(userService.getUsers(pageable));


    }

    @PutMapping("/user")
    public ResponseEntity<String> updateUser(@RequestBody UserUpdateDto userUpdateDto) {
        userService.updateUser(userUpdateDto);

        return ResponseEntity.ok("Success Update");
    }

    @DeleteMapping("/user")
    public ResponseEntity<String> deleteUser() {
        userService.deleteUser();

        return ResponseEntity.ok("Success delete");
    }

    @GetMapping("/loginCheck")
    public ResponseEntity<Void> isLogin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(
                authentication.getPrincipal())) {
            return ResponseEntity.noContent().build(); // HTTP 204
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // HTTP 401
    }

}



