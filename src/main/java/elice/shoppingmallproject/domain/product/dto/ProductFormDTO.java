package elice.shoppingmallproject.domain.product.dto;

import elice.shoppingmallproject.domain.product.entity.Product;
import elice.shoppingmallproject.domain.product.entity.ProductOption;
import lombok.Getter;
import lombok.Setter;


import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ProductFormDTO {

    private Long categoryId;

    private String name;

    private String description;

    private int price;

    private List<ProductOptionDTO> options;

    private String details;

    public static ProductFormDTO fromProduct(Product product) {
        ProductFormDTO productFormDTO = new ProductFormDTO();
        productFormDTO.setCategoryId(product.getCategory().getId());
        productFormDTO.setName(product.getName());
        productFormDTO.setPrice(product.getPrice());
        productFormDTO.setDetails(product.getDetails());

        List<ProductOptionDTO> optionDTOs = new ArrayList<>();

        for (ProductOption option : product.getOptions()) {
            ProductOptionDTO optionDTO = new ProductOptionDTO();
            optionDTO.setOptionSize(option.getOptionSize());
            optionDTO.setStock(option.getStock());
            optionDTOs.add(optionDTO);
        }

        productFormDTO.setOptions(optionDTOs);

        return productFormDTO;
    }
}
