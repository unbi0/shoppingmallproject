package elice.shoppingmallproject.domain.category.entity;

import elice.shoppingmallproject.global.common.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;

@Entity
@Getter
public class Category extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    @Column(nullable = false)
    private String name;


    public static Category addCategory(String name) {
        Category category = new Category();
        category.name = name;

        return category;
    }

    public void updateCategory(String name) {
        this.name = name;
    }
}