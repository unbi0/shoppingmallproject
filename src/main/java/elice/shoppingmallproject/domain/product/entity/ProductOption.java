package elice.shoppingmallproject.domain.product.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;



@Getter
@Setter
@Entity
@Table(name = "product_option")
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
}