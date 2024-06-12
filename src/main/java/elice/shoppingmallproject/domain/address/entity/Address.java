package elice.shoppingmallproject.domain.address.entity;

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

    public static Address createAddress(String postcode, String address, String detailAddress) {
        Address addr = new Address();

        addr.postcode = postcode;
        addr.address = address;
        addr.detailAddress = detailAddress;
        return addr;
    }
}
