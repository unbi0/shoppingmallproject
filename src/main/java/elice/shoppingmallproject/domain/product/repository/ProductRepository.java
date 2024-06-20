package elice.shoppingmallproject.domain.product.repository;


import elice.shoppingmallproject.domain.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT p FROM Product p ORDER BY p.productId DESC")
    List<Product> findAllByOrderByProductIdDesc();

    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId ORDER BY p.productId DESC")
    List<Product> findAllByCategoryIdOrderByProductIdDesc(Long categoryId);

    @Query("SELECT p FROM Product p WHERE " +
            "LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(p.details) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(p.category.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Product> searchProducts(@Param("keyword") String keyword);

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.options WHERE p.productId = :id")
    Product findOptionidById(@Param("id") Long id);
}
