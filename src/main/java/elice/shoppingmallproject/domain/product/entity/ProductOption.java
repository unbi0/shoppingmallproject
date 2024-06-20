package elice.shoppingmallproject.domain.product.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;



@Getter
@Setter
@Entity
public class ProductOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "option_id", columnDefinition = "bigint(20)")
    private Long optionId;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "option_size", length = 255)
    private String optionSize;

    @Column(name = "stock", nullable = false)
    private int stock;

    public static ProductOption option(String optionSize, int stock) {
        ProductOption option = new ProductOption();
        option.setOptionSize(optionSize);
        option.setStock(stock);
        return option;
    }

    //재고가 감소하는 기능 재고숫자를 OrderDetail에서 count로 예상
    public void decreaseStock(int count) {
        if (this.stock < count) {
            throw new IllegalArgumentException("재고가 부족합니다. 사이즈: " + this.optionSize + "재고수량: " + this.stock);
        }
        this.stock -= count;
    }

    public void increaseStock(int count){
        this.stock += count;
    }
}
