package elice.shoppingmallproject.domain.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class UserSignUpDto {

    private String email;
    private String password;
    private String username;
    private String postcode;
    private String address;
    private String detailAddress;

}
