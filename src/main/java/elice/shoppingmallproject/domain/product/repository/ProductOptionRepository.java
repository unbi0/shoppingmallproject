package elice.shoppingmallproject.domain.product.repository;

import elice.shoppingmallproject.domain.product.entity.ProductOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ProductOptionRepository extends JpaRepository<ProductOption, Long> {
    @Modifying
    @Transactional
    @Query("DELETE FROM ProductOption po WHERE po.product.productId = :productId")
    void deleteByProductId(Long productId);

}
