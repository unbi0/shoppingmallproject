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
import java.util.Optional;
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
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderDetailService orderDetailService;

    // 관리자 : 주문 조회
    @GetMapping("/admin")
    public ResponseEntity<List<Orders>> searchAllOrders(@RequestParam(required = false) Long orderId, @RequestParam LocalDateTime startDate, @RequestParam LocalDateTime endDate, @RequestParam(required = false) OrderStatus orderStatus) {
        return ResponseEntity.ok(orderService.searchAllOrders(orderId, startDate, endDate, orderStatus));
    }

    // 사용자 : 주문 조회
    // userId 생성 UserUtil로 바꿔야함
    @GetMapping
    public ResponseEntity<List<Orders>> searchUserOrders(@RequestParam Long userId, @RequestParam(required = false) Long orderId, @RequestParam LocalDateTime startDate, @RequestParam LocalDateTime endDate, @RequestParam(required = false) OrderStatus orderStatus) {
        return ResponseEntity.ok(orderService.searchUserOrders(userId, orderId, startDate, endDate, orderStatus));
    }

    // 주문 ID로 주문상세 조회
    @GetMapping("/{orderId}/detail")
    public ResponseEntity<List<OrderDetail>> getOrderDetailByOrderId(@PathVariable Long orderId){
        return ResponseEntity.ok(orderDetailService.findOrderDetailByOrderId(orderId));
    }

    // 모든 주문상세 조회
    @GetMapping("/detail")
    public ResponseEntity<List<OrderDetail>> getAllOrderDetail(){
        return ResponseEntity.ok(orderDetailService.findAllOrderDetail());
    }

    // 사용자 : 주문 생성 + 주문상세 생성
    @PostMapping
    public ResponseEntity<Orders> createOrder(@RequestBody OrderRequestDto orderRequestDto) {
        return ResponseEntity.ok(orderService.createOrder(orderRequestDto));
    }

    // 사용자 : 주문 수정
    @PutMapping("/{orderId}")
    public ResponseEntity<Orders> updateOrder(@PathVariable Long orderId, @RequestBody OrderRequestDto orderRequestDto) {
        return ResponseEntity.ok(orderService.updateOrder(orderId, orderRequestDto.toOrdersEntity()));
    }

    // 관리자 : 주문 상태 수정
    @PutMapping("/status/{orderId}")
    public ResponseEntity<Orders> updateOrderStatus(@PathVariable Long orderId, @RequestBody OrderStatusUpdateDto orderStatusUpdateDto) {
        OrderStatus newStatus = orderStatusUpdateDto.getOrderStatus();
        return ResponseEntity.ok(orderService.updateOrderStatus(orderId, newStatus));
    }

    // 사용자 : 주문상세 수정
    @PutMapping("/detail/{orderDetailId}")
    public ResponseEntity<OrderDetail> updateOrderDetail(@PathVariable Long orderDetailId, @RequestBody OrderDetailUpdateDto orderDetailUpdateDto) {
        return ResponseEntity.ok(orderDetailService.updateOrderDetail(orderDetailId, orderDetailUpdateDto));
    }

    // 사용자 : 주문 삭제
    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long orderId) {
        orderService.deleteOrder(orderId);
        return ResponseEntity.ok().build();
    }

    // 사용자 : 주문상세 삭제
    @DeleteMapping("/detail/{orderDetailId}")
    public ResponseEntity<Void> deleteOrderDetail(@PathVariable Long orderDetailId) {
        orderDetailService.deleteOrderDetail(orderDetailId);
        return ResponseEntity.ok().build();
    }

}
