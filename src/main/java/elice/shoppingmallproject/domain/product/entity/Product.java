package elice.shoppingmallproject.domain.product.entity;

import elice.shoppingmallproject.domain.category.entity.Category;
import elice.shoppingmallproject.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;

import lombok.ToString;
import java.util.List;

@Entity
@Table(name = "product")
@Getter
@ToString
public class Product extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id", columnDefinition = "bigint(20)")
    private Long productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

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

    public static Product createProduct(Category category, String name, int price, String description, String details) {
        Product product = new Product();
        product.category = category;
        product.name = name;
        product.price = price;
        product.description = description;
        product.details = details;
        return product;
    }

    public static void editProduct(Product product, Category category, String name, int price, String description, String details) {
        product.category = category;
        product.name = name;
        product.price = price;
        product.description = description;
        product.details = details;
    }
}