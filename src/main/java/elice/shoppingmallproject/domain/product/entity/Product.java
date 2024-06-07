package elice.shoppingmallproject.domain.product.entity;

import elice.shoppingmallproject.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;

import lombok.ToString;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name="prodct")
@Getter

@ToString
public class Product extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id", columnDefinition = "bigint(20)")
    private Long productId;

    @Column(name = "category_id", columnDefinition = "bigint(20)")
    private Long categoryId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "price", nullable = false)
    private int price;

    @Column(name = "description", length = 150)
    private String description;

    @Column(name = "details", length = 1000)
    private String details;


    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<ProductOption> options;

    //@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    //private List<ImageEntity> images;

    public static Product createProduct(String name, int price, String description, String details) {
        Product product = new Product();
        product.name = name;
        product.price = price;
        product.description = description;
        product.details = details;

        return product;
        //정적 팩토리 메소드
    }




}


