package elice.shoppingmallproject.domain.image.entity;

import elice.shoppingmallproject.domain.image.dto.ImageDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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
    private Long image_id;

    @ManyToOne
    private Product product;
    //연관관계메서드

    @NotNull
    private String url;
    @NotNull
    private String file_name;

    public ImageDto toDto(){
        return ImageDto.builder()
                .image_id(image_id)
                .product_id(product_id)
                .url(url)
                .file_name(file_name)
                .build();
    }
}

