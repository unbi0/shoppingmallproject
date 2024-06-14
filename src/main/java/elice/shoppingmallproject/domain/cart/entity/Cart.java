package elice.shoppingmallproject.domain.cart.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import elice.shoppingmallproject.domain.user.entity.User;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "cart")
@Getter
@Setter
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_id")
    private Long cartId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "option_id")
    private Long optionId;

    @Column(nullable = false)
    private int quantity;

    // 롬복의 @NoArgsConstructor, @AllArgsConstructor로 대체됨

    // 생성자
    public Cart() {
    }

    public Cart(Long optionId, User user, int quantity) {
        this.optionId = optionId;
        this.quantity = quantity;
        this.user = user;

    }
}