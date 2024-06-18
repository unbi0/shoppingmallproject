package elice.shoppingmallproject.domain.user.dto;

import elice.shoppingmallproject.domain.address.dto.AddressResponseDto;
import elice.shoppingmallproject.domain.address.entity.Address;
import elice.shoppingmallproject.domain.user.entity.Role;
import elice.shoppingmallproject.domain.user.entity.User;
import lombok.Getter;

@Getter
public class UserResponseDto {
    private String email;
    private String username;
    private Role role;  //USER , ADMIN
    private AddressResponseDto address;


    public UserResponseDto(User user, AddressResponseDto address) {
        this.email = user.getEmail();
        this.username = user.getUsername();
        this.role = user.getRole();
        this.address = address;
    }
}
