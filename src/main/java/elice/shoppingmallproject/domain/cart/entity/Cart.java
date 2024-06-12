package elice.shoppingmallproject.domain.cart.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

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
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "option_id", nullable = false)
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
