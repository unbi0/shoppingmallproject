package elice.shoppingmallproject.domain.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class UserSignUpDto {

    private String email;
    private String username;
    private String password;
    private String name;

}
