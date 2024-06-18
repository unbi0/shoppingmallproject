package elice.shoppingmallproject.domain.image.dto;

import elice.shoppingmallproject.domain.image.entity.Image;
import elice.shoppingmallproject.domain.product.entity.Product;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ImageDto {

    private Long image_id;
    private Product product;
    private String url;
    private String file_name;
    private MultipartFile image;

    public Image toEntity(){
        return Image.builder()
                .image_id(image_id)
                .product(product)
                .url(url)
                .fileName(file_name)
                .build();
    }
}

