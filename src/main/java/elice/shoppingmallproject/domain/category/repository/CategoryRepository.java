package elice.shoppingmallproject.domain.category.repository;

import elice.shoppingmallproject.domain.category.entity.Category;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Boolean existsByName(String name);

    @Query("SELECT c FROM Category c ORDER BY c.createAt ASC")
    List<Category> findAllByCreateAt();


}


