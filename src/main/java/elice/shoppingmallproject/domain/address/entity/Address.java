package elice.shoppingmallproject.domain.address.entity;

import elice.shoppingmallproject.domain.address.dto.AddressRequestDto;
import elice.shoppingmallproject.domain.address.dto.AddressResponseDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;

@Entity
@Getter
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    private Long id;

    private String postcode;
    private String address;
    private String detailAddress;

    public static Address createAddress(AddressRequestDto addressRequestDto) {

        Address addr = new Address();

        addr.postcode = addressRequestDto.getPostcode();
        addr.address = addressRequestDto.getAddress();
        addr.detailAddress = addressRequestDto.getDetailAddress();
        return addr;
    }

    public void updateAddress(AddressRequestDto addressRequestDto) {
        this.postcode = addressRequestDto.getPostcode();
        this.address = addressRequestDto.getAddress();
        this.detailAddress = addressRequestDto.getDetailAddress();
    }
}
