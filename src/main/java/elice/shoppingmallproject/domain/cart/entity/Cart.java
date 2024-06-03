package elice.shoppingmallproject.domain.cart.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "cart") // 테이블 이름을 "cart"로 지정
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_id") // 컬럼 이름을 "cart_id"로 지정
    private Long cartId;

    @Column(name = "option_id", nullable = false) // 컬럼 이름을 "option_id"로 지정하고, null 허용하지 않음
    private Long optionId;

    @Column(name = "user_id") // 컬럼 이름을 "user_id"로 지정하고, null 허용함
    private Long userId;

    @Column(nullable = false) // null 허용하지 않음
    private int quantity;

    // 생성자, Getter 및 Setter 메서드는 생략하겠습니다.
    // 필요에 따라 추가해주세요.

    // 생성자
    public Cart() {
    }

    public Cart(Long optionId, Long userId, int quantity) {
        this.optionId = optionId;
        this.userId = userId;
        this.quantity = quantity;
    }

    // Getter 및 Setter 메서드
    public Long getCartId() {
        return cartId;
    }

    public void setCartId(Long cartId) {
        this.cartId = cartId;
    }

    public Long getOptionId() {
        return optionId;
    }

    public void setOptionId(Long optionId) {
        this.optionId = optionId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
