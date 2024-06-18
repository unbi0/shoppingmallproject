package elice.shoppingmallproject.domain.user.dto;

import elice.shoppingmallproject.domain.address.dto.AddressRequestDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class UserSignUpDto {

    private String email;
    private String password;
    private String username;
    private AddressRequestDto address;

}
