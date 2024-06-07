package elice.shoppingmallproject.domain.product.dto;

import elice.shoppingmallproject.domain.product.entity.Product;
import elice.shoppingmallproject.global.common.BaseTimeEntity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ProductDTO extends BaseTimeEntity {
    private Long productId;

    private String name;

    private int price;

    private String description;

    private String details;


    public static ProductDTO toProductDTO(Product product) {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setProductId(product.getProductId());
        productDTO.setName(product.getName());
        productDTO.setPrice(product.getPrice());
        productDTO.setDescription(product.getDescription());
        productDTO.setDetails(product.getDetails());
        return productDTO;
    }
}