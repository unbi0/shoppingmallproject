package elice.shoppingmallproject.domain.product.dto;

import lombok.Getter;
import lombok.Setter;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.List;

@Getter
@Setter
public class ProductFormDTO {

    private Long productId;

    @NotNull(message = "카테고리는 필수 입력 값입니다.")
    private Long categoryId;

    @NotNull(message = "상품명은 필수 입력 값입니다.")
    private String name;

    private String description;

    @NotNull(message = "상품 상세는 필수 입력 값입니다.")
    private int price;

    @NotNull(message = "사이즈는 필수 입력 입니다.")
    private int optionSize;

    @NotNull(message = "재고는 필수 입력 입니다.")
    private int stock;

    private String details;

    private List<ImageDto> images;
}

