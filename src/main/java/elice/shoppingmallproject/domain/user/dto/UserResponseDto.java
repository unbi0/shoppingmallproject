package elice.shoppingmallproject.domain.user.dto;

import elice.shoppingmallproject.domain.address.dto.AddressResponseDto;
import elice.shoppingmallproject.domain.user.entity.User;
import lombok.Getter;

@Getter
public class UserResponseDto {
    private String email;
    private String username;
    private AddressResponseDto address;


    public UserResponseDto(User user) {
        email = user.getEmail();
        username = user.getUsername();
        address = new AddressResponseDto(user.getAddress());
    }

}
