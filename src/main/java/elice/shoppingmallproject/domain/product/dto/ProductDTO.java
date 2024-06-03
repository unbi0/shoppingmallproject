package elice.shoppingmallproject.domain.product.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ProductDTO {
    private Long productId;

    private String name;

    private int price;

    private String description;

    private String details;

    private LocalDateTime createDate;

    private LocalDateTime lastModifiedDate;

}
