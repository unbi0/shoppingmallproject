package elice.shoppingmallproject.domain.order.service;

import elice.shoppingmallproject.domain.order.dto.OrderRequestDto;
import elice.shoppingmallproject.domain.order.entity.OrderStatus;
import elice.shoppingmallproject.domain.order.entity.Orders;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderService {

    // 관리자 : 모든 주문 조회
    List<Orders> findAllOrders();

    // 사용자 : 자신의 모든 주문내역 조회
    List<Orders> findAllUserOrders(Long userId);

    // 주문번호로 조회
    Optional<Orders> findOrderById(Long id);

    // 주문날짜 기간으로 조회
    List<Orders> findByCreatedAt(LocalDateTime startDate, LocalDateTime endDate);

    // 주문상태로 조회
    List<Orders> findByOrderStatus(OrderStatus orderStatus);

    // 사용자 : 주문 생성
    Orders createOrder(OrderRequestDto orderRequestDto);

    // 사용자 : 주문 삭제
    void deleteOrder(Long id);

    // 관리자 : 주문 상태 수정
    Orders updateOrderStatus(Long id, String status);

    // 사용자 : 주문 수정
    Orders updateOrder(Long id, Orders updatedOrders);
}
