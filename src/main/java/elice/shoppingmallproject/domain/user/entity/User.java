package elice.shoppingmallproject.domain.user.entity;

import elice.shoppingmallproject.domain.address.entity.Address;
import elice.shoppingmallproject.domain.user.dto.UserUpdateDto;
import elice.shoppingmallproject.global.common.BaseTimeEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;
    private String username;

    private String email;
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id")
    private Address address;


    public static User createUser(String username, String email, String password, Address address, Role role) {
        User user = new User();
        user.username = username;
        user.email = email;
        user.password = password;
        user.address = address;
        user.role = role;
        return user;
    }


    public void updateUser(UserUpdateDto userUpdateDto) {
        this.username = userUpdateDto.getUsername();
        if (this.address != null) {
            this.address.updateAddress(userUpdateDto.getAddress());
        } else {
            this.address = Address.createAddress(userUpdateDto.getAddress());
        }

    }

    public void passwordEncode(BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.password = bCryptPasswordEncoder.encode(this.password);

    }
}