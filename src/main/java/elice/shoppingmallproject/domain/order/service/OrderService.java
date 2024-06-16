package elice.shoppingmallproject.domain.order.service;

import elice.shoppingmallproject.domain.order.dto.OrderListDto;
import elice.shoppingmallproject.domain.order.dto.OrderRequestDto;
import elice.shoppingmallproject.domain.order.dto.OrderResponseDto;
import elice.shoppingmallproject.domain.order.dto.OrderStatusUpdateDto;
import elice.shoppingmallproject.domain.order.dto.OrderUpdateDto;
import elice.shoppingmallproject.domain.order.entity.OrderStatus;
import elice.shoppingmallproject.domain.order.entity.Orders;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderService {

    // 관리자 : 주문 조회
    List<OrderListDto> searchAllOrders(Long orderId, LocalDateTime startDate, LocalDateTime endDate, OrderStatus orderStatus);

    // 주문 ID로 주문 조회
    Optional<OrderResponseDto> findOrderById(Long orderId);

    // 사용자 : 주문 조회
//    List<Orders> searchUserOrders(Long orderId, LocalDateTime startDate, LocalDateTime endDate, OrderStatus orderStatus);
    List<OrderListDto> searchUserOrders(Long userId, Long orderId, LocalDateTime startDate, LocalDateTime endDate, OrderStatus orderStatus);

    // 사용자 : 주문 생성
    Orders createOrder(OrderRequestDto orderRequestDto);

    // 사용자 : 주문 삭제
    void deleteOrder(Long orderId);

    // 관리자 : 주문 상태 수정
    void updateOrderStatus(Long orderId, OrderStatusUpdateDto orderStatusUpdateDto);

    // 사용자 : 주문 수정
    void updateOrder(Long orderId, OrderUpdateDto orderUpdateDto);
}
