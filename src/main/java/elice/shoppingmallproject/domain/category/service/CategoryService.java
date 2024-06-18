package elice.shoppingmallproject.domain.category.service;

import elice.shoppingmallproject.domain.category.dto.CategoryResponseDto;
import elice.shoppingmallproject.domain.category.entity.Category;
import elice.shoppingmallproject.domain.category.repository.CategoryRepository;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryResponseDto createCategory(String name) {

        if (categoryRepository.existsByName(name)) {
            throw new IllegalArgumentException("이미 존재하는 카테고리 이름입니다.");
        }

        Category category = Category.addCategory(name);
        categoryRepository.save(category);

        return new CategoryResponseDto(category);
    }

    public CategoryResponseDto updateCategory(Long categoryId, String name) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NoSuchElementException("조회되는 카테고리가 없습니다."));

        category.updateCategory(name);


        return new CategoryResponseDto(category);
    }

    public List<CategoryResponseDto> getCategories() {
        List<Category> categories = categoryRepository.findAllByCreateAt();

        List<CategoryResponseDto> result = categories.stream()
                .map(CategoryResponseDto::new)
                .toList();

        return result;

    }

    public void deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NoSuchElementException("조회되는 카테고리가 없습니다."));

        categoryRepository.delete(category);
    }
}

