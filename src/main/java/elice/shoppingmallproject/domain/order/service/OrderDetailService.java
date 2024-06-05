package elice.shoppingmallproject.domain.order.service;

import elice.shoppingmallproject.domain.order.entity.OrderDetail;
import java.util.List;
import java.util.Optional;

public interface OrderDetailService {

    // 모든 주문상세 조회
    List<OrderDetail> findAllOrderDetails();

    // ID로 주문상세 조회
    Optional<OrderDetail> findOrderDetailById(Long id);

    // 주문상세 생성
    OrderDetail createOrderDetail(OrderDetail orderDetail);

    // 주문상세 삭제
    void deleteOrderDetail(Long id);

    // 주문상세 수정
    OrderDetail updateOrderDetail(Long id, OrderDetail updatedOrderDetail);
}
