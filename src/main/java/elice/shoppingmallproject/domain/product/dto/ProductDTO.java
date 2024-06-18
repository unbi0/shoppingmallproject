package elice.shoppingmallproject.domain.product.dto;

import elice.shoppingmallproject.domain.product.entity.Product;
import elice.shoppingmallproject.global.common.BaseTimeEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDTO {
    private Long productId;

    private String name;

    private int price;

    private String description;

    private String details;

    private String imageUrl;

    public static ProductDTO toProductDTO(Product product) {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setProductId(product.getProductId());
        productDTO.setName(product.getName());
        productDTO.setPrice(product.getPrice());
        productDTO.setDescription(product.getDescription());
        productDTO.setDetails(product.getDetails());

        if (product.getImages() != null && !product.getImages().isEmpty()) {
            productDTO.setImageUrl(product.getImages().get(0).getUrl());  // 첫 번째 이미지의 URL을 설정
        }

        return productDTO;
    }
}
