package elice.shoppingmallproject.domain.cart.repository;

import jakarta.transaction.Transactional;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import elice.shoppingmallproject.domain.cart.entity.Cart;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    List<Cart> findByUserId(Long userId);

    @Query("SELECT c FROM Cart c WHERE c.product.productId = :productId")
    List<Cart> findByProductId(@Param("productId") Long productId);

    @Query("SELECT c FROM Cart c WHERE c.id = :cartId AND c.user.id = :userId")
    Cart findByCartIdAndUserId(@Param("cartId") Long cartId, @Param("userId") Long userId);

    @Transactional
    void deleteByUserId(Long userId);
}