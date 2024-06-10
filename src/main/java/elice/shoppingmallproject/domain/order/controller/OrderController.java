package elice.shoppingmallproject.domain.order.controller;

import elice.shoppingmallproject.domain.order.dto.OrderDetailRequestDto;
import elice.shoppingmallproject.domain.order.dto.OrderDetailUpdateDto;
import elice.shoppingmallproject.domain.order.dto.OrderRequestDto;
import elice.shoppingmallproject.domain.order.dto.OrderStatusUpdateDto;
import elice.shoppingmallproject.domain.order.entity.OrderDetail;
import elice.shoppingmallproject.domain.order.entity.OrderStatus;
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
    @GetMapping("/all")
    public ResponseEntity<List<Orders>> getAllOrders() {
        return ResponseEntity.ok(orderService.findAllOrders());
    }

    @GetMapping("/detail/all")
    public ResponseEntity<List<OrderDetail>> getAllOrderDetail(){
        return ResponseEntity.ok(orderDetailService.findAllOrderDetail());
    }

    // 관리자 : 주문 상태 수정
    @PutMapping("/status/update/{orderId}")
    public ResponseEntity<Orders> updateOrderStatus(@PathVariable Long orderId, @RequestBody OrderStatusUpdateDto orderStatusUpdateDto) {
        OrderStatus newStatus = orderStatusUpdateDto.getOrderStatus();
        return ResponseEntity.ok(orderService.updateOrderStatus(orderId, newStatus));
    }

    // 사용자 : 자신의 모든 주문내역 조회
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Orders>> getAllUserOrders(@PathVariable Long userId) {
        return ResponseEntity.ok(orderService.findAllUserOrders(userId));
    }

    // 사용자 : 주문 생성
    @PostMapping("/create")
    public ResponseEntity<Orders> createOrder(@RequestBody OrderRequestDto orderRequestDto) {
        return ResponseEntity.ok(orderService.createOrder(orderRequestDto));
    }

    // 사용자 : 주문 삭제
    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long orderId) {
        orderService.deleteOrder(orderId);
        return ResponseEntity.ok().build();
    }

    // 사용자 : 주문 수정
    @PutMapping("/update/{orderId}")
    public ResponseEntity<Orders> updateOrder(@PathVariable Long orderId, @RequestBody OrderRequestDto orderRequestDto) {
        return ResponseEntity.ok(orderService.updateOrder(orderId, orderRequestDto.toOrdersEntity()));
    }

    // 사용자 : 주문상세 수정
    @PutMapping("/detail/update/{orderDetailId}")
    public ResponseEntity<OrderDetail> updateOrderDetail(@PathVariable Long orderDetailId, @RequestBody OrderDetailUpdateDto orderDetailUpdateDto) {
        return ResponseEntity.ok(orderDetailService.updateOrderDetail(orderDetailId, orderDetailUpdateDto));
    }

    // 사용자 : 주문상세 삭제
    @DeleteMapping("/detail/{orderDetailId}")
    public ResponseEntity<Void> deleteOrderDetail(@PathVariable Long orderDetailId) {
        orderDetailService.deleteOrderDetail(orderDetailId);
        return ResponseEntity.ok().build();
    }

    // 주문번호로 주문 조회
    @GetMapping("/{orderId}")
    public ResponseEntity<Orders> getOrderById(@PathVariable Long orderId) {
        return orderService.findOrderById(orderId)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 주문 ID로 주문상세 조회
    @GetMapping("/{orderId}/detail")
    public ResponseEntity<OrderDetail> getOrderDetailByOrderId(@PathVariable Long orderId){
        return orderDetailService.findOrderDetailByOrderId(orderId)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 주문 날짜 기간으로 조회
    @GetMapping("/date")
    public ResponseEntity<List<Orders>> getOrdersByDateRange(@RequestParam LocalDateTime startDate, @RequestParam LocalDateTime endDate) {
        return ResponseEntity.ok(orderService.findByCreatedAt(startDate, endDate));
    }

    // 주문상태로 조회
    @GetMapping("/status")
    public ResponseEntity<List<Orders>> getOrdersByStatus(@RequestParam String status) {
        return ResponseEntity.ok(orderService.findByOrderStatus(Enum.valueOf(OrderStatus.class, status.toUpperCase())));
    }

}
