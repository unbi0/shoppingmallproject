package elice.shoppingmallproject.domain.address.dto;

import elice.shoppingmallproject.domain.address.entity.Address;
import lombok.Getter;

@Getter
public class AddressResponseDto {
    private Long addressId;
    private String postcode;
    private String address;
    private String detailAddress;

    public AddressResponseDto(Address address) {
        this.addressId = address.getId();
        this.postcode = address.getPostcode();
        this.address = address.getAddress();
        this.detailAddress = address.getDetailAddress();
    }
}
