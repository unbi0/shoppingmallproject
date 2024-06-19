package elice.shoppingmallproject.domain.order.controller;

import elice.shoppingmallproject.domain.order.dto.OrderListDto;
import elice.shoppingmallproject.domain.order.dto.OrderRequestDto;
import elice.shoppingmallproject.domain.order.dto.OrderResponseDto;
import elice.shoppingmallproject.domain.order.dto.OrderStatusUpdateDto;
import elice.shoppingmallproject.domain.order.dto.OrderUpdateDto;
import elice.shoppingmallproject.domain.order.entity.OrderStatus;
import elice.shoppingmallproject.domain.order.entity.Orders;
import elice.shoppingmallproject.domain.order.service.OrderDetailService;
import elice.shoppingmallproject.domain.order.service.OrderService;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderDetailService orderDetailService;

    // 관리자 : 주문 조회
    @GetMapping("/admin/orders")
    public Page<OrderListDto> searchAllOrders(@RequestParam(required = false) Long orderId,
        @RequestParam(required = false) LocalDateTime startDate,
        @RequestParam(required = false) LocalDateTime endDate,
        @RequestParam(required = false) OrderStatus orderStatus,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size) {
        return orderService.searchAllOrders(orderId, startDate, endDate, orderStatus, page, size);
    }

    // 주문 ID로 주문 조회
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<Optional<OrderResponseDto>> findOrderById(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.findOrderById(orderId));
    }

    // 사용자 : 주문 조회
    @GetMapping("/orders")
    public Page<OrderListDto> searchUserOrders(
        @RequestParam(required = false) Long orderId,
        @RequestParam(required = false) LocalDateTime startDate,
        @RequestParam(required = false) LocalDateTime endDate,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size) {
        return orderService.searchUserOrders(orderId, startDate, endDate, page, size);
    }

    // 사용자 : 주문 생성 + 주문상세 생성
    @PostMapping("/orders")
    public ResponseEntity<Orders> createOrder(@RequestBody OrderRequestDto orderRequestDto) {
        return ResponseEntity.ok(orderService.createOrder(orderRequestDto));
    }

    // 사용자 : 주문 수정
    @PutMapping("/orders/{orderId}")
    public void updateOrder(@PathVariable Long orderId, @RequestBody OrderUpdateDto orderUpdateDto) {
        orderService.updateOrder(orderId, orderUpdateDto);
    }

    // 관리자 : 주문 상태 수정
    @PutMapping("/orders/status/{orderId}")
    public void updateOrderStatus(@PathVariable Long orderId, @RequestBody OrderStatusUpdateDto orderStatusUpdateDto) {
        orderService.updateOrderStatus(orderId, orderStatusUpdateDto);
    }

    // 사용자 : 주문 삭제
    @DeleteMapping("/orders/{orderId}")
    public void deleteOrder(@PathVariable Long orderId) {
        orderService.deleteOrder(orderId);
    }

}
