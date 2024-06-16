package elice.shoppingmallproject.domain.image.repository;

import elice.shoppingmallproject.domain.image.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ImageRepository extends JpaRepository<Image, Long> {
    @Query("SELECT COUNT(i) FROM Image i WHERE i.product_id = :product_id")
    long countByProductId(@Param("product_id") Long product_id);
}

