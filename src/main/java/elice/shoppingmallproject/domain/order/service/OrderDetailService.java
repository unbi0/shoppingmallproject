package elice.shoppingmallproject.domain.order.service;

import elice.shoppingmallproject.domain.order.entity.OrderDetail;
import java.util.List;

public interface OrderDetailService {

    // 모든 주문상세 조회
    List<OrderDetail> findAllOrderDetail();

    // 주문 ID로 주문상세 조회
    List<OrderDetail> findOrderDetailByOrderId(Long orderId);
}
