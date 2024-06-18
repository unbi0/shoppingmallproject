package elice.shoppingmallproject.domain.image.entity;

import elice.shoppingmallproject.domain.image.dto.ImageDto;
import elice.shoppingmallproject.domain.product.entity.Product;
import jakarta.persistence.*;
import lombok.*;
import org.antlr.v4.runtime.misc.NotNull;

@Entity
@Data
@Table(name = "image")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Image {

    //관계맺기 설정
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id", columnDefinition = "bigint(20)")
    private Long image_id;

    // 연관관계 설정 메서드
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @NotNull
    private String url;

    @NotNull
    private String fileName;

    public ImageDto toDto() {
        return ImageDto.builder()
                .image_id(image_id)
                .product(product)
                .url(url)
                .file_name(fileName)
                .build();
    }

}
//연관관계메서드
