package elice.shoppingmallproject.domain.user.controller;

import elice.shoppingmallproject.domain.user.dto.UserSignUpDto;
import elice.shoppingmallproject.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/sign-up")
    public ResponseEntity<String> signUp(@RequestBody UserSignUpDto userSignUpDto) {
        userService.signUp(userSignUpDto);

        return ResponseEntity.status(HttpStatus.CREATED).body("Created");
    }
}