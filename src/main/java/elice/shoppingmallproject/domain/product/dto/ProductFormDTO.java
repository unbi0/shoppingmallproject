package elice.shoppingmallproject.domain.product.dto;

import lombok.Getter;
import lombok.Setter;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.List;

@Getter
@Setter
public class ProductFormDTO {

    private Long productId;


    private Long categoryId;


    private String name;

    private String description;


    private int price;


    private int optionSize;


    private int stock;

    private String details;

    //private List<ImageDto> images;
}

