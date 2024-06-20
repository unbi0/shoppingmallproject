package elice.shoppingmallproject.domain.product.dto;

import elice.shoppingmallproject.domain.image.entity.Image;
import elice.shoppingmallproject.domain.product.entity.Product;
import elice.shoppingmallproject.domain.product.entity.ProductOption;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class ProductDTO {
    private Long productId;
    private String name;
    private int price;
    private String description;
    private String details;
    private List<String> imageUrl; // 변경된 부분
    private List<Long> optionId; // 추가된 부분

    public static ProductDTO toProductDTO(Product product) {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setProductId(product.getProductId());
        productDTO.setName(product.getName());
        productDTO.setPrice(product.getPrice());
        productDTO.setDescription(product.getDescription());
        productDTO.setDetails(product.getDetails());

        // 이미지 URL 리스트를 설정
        if (product.getImages() != null && !product.getImages().isEmpty()) {
            productDTO.setImageUrl(product.getImages().stream()
                    .map(Image::getUrl)
                    .collect(Collectors.toList()));
        }

        // ProductOption의 optionId 값을 리스트로 설정
        if (product.getOptions() != null && !product.getOptions().isEmpty()) {
            productDTO.setOptionId(product.getOptions().stream()
                    .map(ProductOption::getOptionId)
                    .collect(Collectors.toList()));
        }

        return productDTO;
    }
}
