package elice.shoppingmallproject.domain.address.dto;

import lombok.Getter;

@Getter
public class AddressRequestDto {
    private String postcode;
    private String address;
    private String detailAddress;
}