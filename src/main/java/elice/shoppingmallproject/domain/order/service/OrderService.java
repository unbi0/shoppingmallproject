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
import org.springframework.data.domain.Page;

public interface OrderService {

    // 관리자 : 주문 조회
    Page<OrderListDto> searchAllOrders(Long orderId, LocalDateTime startDate, LocalDateTime endDate, OrderStatus orderStatus, int page, int size);

    // 주문 ID로 주문 조회
    Optional<OrderResponseDto> findOrderById(Long orderId);

    // 사용자 : 주문 조회
    Page<OrderListDto> searchUserOrders(Long orderId, LocalDateTime startDate, LocalDateTime endDate, int page, int size);

    // 사용자 : 주문 생성
    Orders createOrder(OrderRequestDto orderRequestDto);

    // 사용자 : 주문 삭제
    void deleteOrder(Long orderId);

    // 관리자 : 주문 상태 수정
    void updateOrderStatus(Long orderId, OrderStatusUpdateDto orderStatusUpdateDto);

    // 사용자 : 주문 수정
    void updateOrder(Long orderId, OrderUpdateDto orderUpdateDto);
}
