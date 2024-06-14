package elice.shoppingmallproject.domain.user.dto;

import elice.shoppingmallproject.domain.address.dto.AddressRequestDto;
import elice.shoppingmallproject.domain.address.entity.Address;
import lombok.Getter;

@Getter
public class UserUpdateDto {
    private String username;
    private AddressRequestDto address;
}
