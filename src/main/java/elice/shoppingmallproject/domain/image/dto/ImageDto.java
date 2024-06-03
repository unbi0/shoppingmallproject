package elice.shoppingmallproject.domain.image.dto;

import elice.shoppingmallproject.domain.image.entity.Image;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ImageDto {

    private Long image_id;
    private Long product_id;
    private String url;
    private String file_name;

    public Image toEntity(){
        return Image.builder()
                .image_id(image_id)
                .product_id(product_id)
                .url(url)
                .file_name(file_name)
                .build();
    }
}

