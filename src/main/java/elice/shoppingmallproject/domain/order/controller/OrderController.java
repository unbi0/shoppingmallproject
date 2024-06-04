package elice.shoppingmallproject.domain.order.controller;

import elice.shoppingmallproject.domain.order.dto.OrderRequestDto;
import elice.shoppingmallproject.domain.order.entity.Orders;
import elice.shoppingmallproject.domain.order.service.OrderDetailService;
import elice.shoppingmallproject.domain.order.service.OrderService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderDetailService orderDetailService;

    // 관리자 : 모든 주문내역 조회
    @GetMapping
    public ResponseEntity<List<Orders>> getAllOrders() {
        return ResponseEntity.ok(orderService.findAllOrders());
    }

    // 관리자 : 주문 상태 수정
    @PutMapping("/{id}")
    public ResponseEntity<Orders> updateOrderStatus(@PathVariable Long id, @RequestParam String status) {
        return ResponseEntity.ok(orderService.updateOrderStatus(id, status));
    }

    // 사용자 : 자신의 모든 주문내역 조회
    @GetMapping("/{userId}")
    public ResponseEntity<List<Orders>> getAllUserOrders(@PathVariable Long userId) {
        return ResponseEntity.ok(orderService.findAllUserOrders(userId));
    }

    // 사용자 : 주문 생성
    @PostMapping("/create")
    public ResponseEntity<Orders> createOrder(@RequestBody OrderRequestDto orderRequestDto) {
        return ResponseEntity.ok(orderService.createOrder(orderRequestDto));
    }

    // 사용자 : 주문 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.ok().build();
    }

    // 사용자 : 주문 수정
    @PutMapping("/{id}")
    public ResponseEntity<Orders> updateOrder(@PathVariable Long id, @RequestBody OrderRequestDto orderRequestDto) {
        return ResponseEntity.ok(orderService.updateOrder(id, orderRequestDto.toOrdersEntity()));
    }

    // 주문번호로 주문 조회
    @GetMapping("/{id}")
    public ResponseEntity<Orders> getOrderById(@PathVariable Long id) {
        return orderService.findOrderById(id)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 주문 날짜 기간으로 조회
    @GetMapping
    public ResponseEntity<List<Orders>> getOrdersByDateRange(@RequestParam LocalDateTime startDate, @RequestParam LocalDateTime endDate) {
        return ResponseEntity.ok(orderService.findByCreatedAt(startDate, endDate));
    }

    // 주문상태로 조회
    @GetMapping
    public ResponseEntity<List<Orders>> getOrdersByStatus(@RequestParam String status) {
        return ResponseEntity.ok(orderService.findByOrderStatus(Enum.valueOf(Orders.OrderStatus.class, status.toUpperCase())));
    }

}
