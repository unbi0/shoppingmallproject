package elice.shoppingmallproject.domain.product.entity;

import elice.shoppingmallproject.domain.category.entity.Category;
import elice.shoppingmallproject.domain.image.entity.Image;
import elice.shoppingmallproject.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;

import lombok.ToString;

import java.util.ArrayList;
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

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Image> images = new ArrayList<>();

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "price", nullable = false)
    private int price;

    @Column(name = "description", length = 150)
    private String description;

    @Column(name = "details", length = 1000)
    private String details;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<ProductOption> options = new ArrayList<>();

    public void addProductOption(ProductOption productOption) {
        if (options == null) {
            options = new ArrayList<>();
        }
        options.add(productOption);
        productOption.setProduct(this);
    }

    public static Product createProduct(Category category, String name, int price, String description, String details,
                                        List<ProductOption> productOptions, List<Image> images) {
        Product product = new Product();
        product.category = category;
        product.name = name;
        product.price = price;
        product.description = description;
        product.details = details;
        if (productOptions != null) {
            productOptions.forEach(product::addProductOption);
        }
        if (images != null) {
            images.forEach(product::addImage);
        }
        return product;
    }

    public static void editProduct(Product product, Category category, String name, int price, String description, String details) {
        product.category = category;
        product.name = name;
        product.price = price;
        product.description = description;
        product.details = details;
    }

    // 연관관계 편의 메서드
//    public void addImage(Image image) {
//        this.images.add(image);  // Product의 images 리스트에 Image를 추가
//        image.setProduct(this);  // Image의 product 필드를 현재 Product로 설정
//    }

    public void addImage(Image image) {
        if (images == null) {
            images = new ArrayList<>();
        }
        images.add(image);
        image.setProduct(this);
    }

    public void removeImage(Image image) {
        this.images.remove(image);
        image.setProduct(null);
    }

    public void addOption(ProductOption option) {
        this.options.add(option);
        option.setProduct(this);
    }

    public void removeOption(ProductOption option) {
        this.options.remove(option);
        option.setProduct(null);
    }
}

