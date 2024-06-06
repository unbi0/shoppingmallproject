package elice.shoppingmallproject.domain.order.repository;

import elice.shoppingmallproject.domain.order.entity.OrderDetail;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {

    Optional<OrderDetail> findByOrderId(Long orderId);

}
