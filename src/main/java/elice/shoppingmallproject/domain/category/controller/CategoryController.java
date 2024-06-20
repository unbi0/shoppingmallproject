package elice.shoppingmallproject.domain.category.controller;

import elice.shoppingmallproject.domain.category.dto.CategoryRequestDto;
import elice.shoppingmallproject.domain.category.dto.CategoryResponseDto;
import elice.shoppingmallproject.domain.category.service.CategoryService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("/admin/category")
    public ResponseEntity<CategoryResponseDto> createCategory(HttpServletRequest request, @RequestBody CategoryRequestDto categoryRequestDto) {
        CategoryResponseDto category = categoryService.createCategory(categoryRequestDto.getName());

        return ResponseEntity.ok(category);
    }

    @GetMapping("/category")
    public ResponseEntity<List<CategoryResponseDto>> getCategories() {
        List<CategoryResponseDto> categories = categoryService.getCategories();

        return ResponseEntity.ok(categories);
    }

    @PatchMapping("/admin/category/{categoryId}")
    public ResponseEntity<CategoryResponseDto> updateCategory(@RequestBody CategoryRequestDto categoryRequestDto,
                                                              @PathVariable Long categoryId) {

        return ResponseEntity.ok(categoryService.updateCategory(categoryId,
                categoryRequestDto.getName()));
    }

    @DeleteMapping("/admin/category/{categoryId}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long categoryId) {
        categoryService.deleteCategory(categoryId);

        return ResponseEntity.noContent().build();
    }
}
