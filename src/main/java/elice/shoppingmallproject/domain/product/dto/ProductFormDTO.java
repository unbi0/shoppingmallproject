package elice.shoppingmallproject.domain.product.dto;

import lombok.Getter;
import lombok.Setter;


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



}


