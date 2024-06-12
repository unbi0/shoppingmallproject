package elice.shoppingmallproject.domain.order.service;

import elice.shoppingmallproject.domain.order.dto.OrderDetailUpdateDto;
import elice.shoppingmallproject.domain.order.entity.OrderDetail;
import java.util.List;

public interface OrderDetailService {

    // 모든 주문상세 조회
    List<OrderDetail> findAllOrderDetail();

    // 주문 ID로 주문상세 조회
    List<OrderDetail> findOrderDetailByOrderId(Long orderId);

    // 주문상세 삭제
    void deleteOrderDetail(Long orderDetailId);

    // 주문상세 수정
    OrderDetail updateOrderDetail(Long orderDetailId, OrderDetailUpdateDto orderDetailUpdateDto);
}
