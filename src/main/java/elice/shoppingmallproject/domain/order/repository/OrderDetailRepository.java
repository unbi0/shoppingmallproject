package elice.shoppingmallproject.domain.order.repository;

import elice.shoppingmallproject.domain.order.entity.OrderDetail;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {

    List<OrderDetail> findByOrders_Id(Long orderId);

}
