package elice.shoppingmallproject.domain.order.service;

import elice.shoppingmallproject.domain.order.dto.OrderRequestDto;
import elice.shoppingmallproject.domain.order.entity.OrderStatus;
import elice.shoppingmallproject.domain.order.entity.Orders;
import java.time.LocalDateTime;
import java.util.List;

public interface OrderService {

    // 관리자 : 주문 조회
    List<Orders> searchAllOrders(Long orderId, LocalDateTime startDate, LocalDateTime endDate, OrderStatus orderStatus);

    // 사용자 : 주문 조회
    List<Orders> searchUserOrders(Long orderId, LocalDateTime startDate, LocalDateTime endDate, OrderStatus orderStatus);

    // 사용자 : 주문 생성
    Orders createOrder(OrderRequestDto orderRequestDto);

    // 사용자 : 주문 삭제
    void deleteOrder(Long orderId);

    // 관리자 : 주문 상태 수정
    Orders updateOrderStatus(Long orderId, OrderStatus status);

    // 사용자 : 주문 수정
    Orders updateOrder(Long orderId, Orders updatedOrders);



}
