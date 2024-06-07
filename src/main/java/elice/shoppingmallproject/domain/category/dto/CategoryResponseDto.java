package elice.shoppingmallproject.domain.category.dto;

import elice.shoppingmallproject.domain.category.entity.Category;
import lombok.Getter;

@Getter
public class CategoryResponseDto {
    private Long categoryId;
    private String name;


    public CategoryResponseDto(Category category) {
        categoryId = category.getId();
        name = category.getName();
    }
}
