package elice.shoppingmallproject.domain.cart.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import elice.shoppingmallproject.domain.cart.entity.Cart;


@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    List<Cart> findByUserId(Long userId);
    //void deleteByCartIds(List<Long> cartIds);
    @Query("SELECT c FROM Cart c WHERE c.cartId = :cartId AND c.user.id = :userId")
    Cart findByCartIdAndUserId(@Param("cartId") Long cartId, @Param("userId") Long userId);

}