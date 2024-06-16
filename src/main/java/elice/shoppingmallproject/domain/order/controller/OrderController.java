package elice.shoppingmallproject.domain.order.controller;

import elice.shoppingmallproject.domain.order.dto.OrderDetailUpdateDto;
import elice.shoppingmallproject.domain.order.dto.OrderListDto;
import elice.shoppingmallproject.domain.order.dto.OrderRequestDto;
import elice.shoppingmallproject.domain.order.dto.OrderResponseDto;
import elice.shoppingmallproject.domain.order.dto.OrderStatusUpdateDto;
import elice.shoppingmallproject.domain.order.dto.OrderUpdateDto;
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
import org.springframework.web.bind.annotation.CrossOrigin;
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
@CrossOrigin(origins = "http://127.0.0.1:5500")
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderDetailService orderDetailService;

    // 관리자 : 주문 조회
    @GetMapping("/admin")
    public ResponseEntity<List<OrderListDto>> searchAllOrders(@RequestParam(required = false) Long orderId, @RequestParam(required = false) LocalDateTime startDate, @RequestParam(required = false) LocalDateTime endDate, @RequestParam(required = false) OrderStatus orderStatus) {
        return ResponseEntity.ok(orderService.searchAllOrders(orderId, startDate, endDate, orderStatus));
    }

    // 주문 ID로 주문 조회
    @GetMapping("/{orderId}")
    public ResponseEntity<Optional<OrderResponseDto>> findOrderById(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.findOrderById(orderId));
    }

    // 사용자 : 주문 조회
//    @GetMapping
//    public ResponseEntity<List<Orders>> searchUserOrders(@RequestParam(required = false) Long orderId, @RequestParam LocalDateTime startDate, @RequestParam LocalDateTime endDate, @RequestParam(required = false) OrderStatus orderStatus) {
//        return ResponseEntity.ok(orderService.searchUserOrders(orderId, startDate, endDate, orderStatus));
//    }

    // 사용자 : 주문 조회
    @GetMapping
    public ResponseEntity<List<OrderListDto>> searchUserOrders(@RequestParam Long userId, @RequestParam(required = false) Long orderId, @RequestParam(required = false) LocalDateTime startDate, @RequestParam(required = false) LocalDateTime endDate, @RequestParam(required = false) OrderStatus orderStatus) {
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
    public void updateOrder(@PathVariable Long orderId, @RequestBody OrderUpdateDto orderUpdateDto) {
        orderService.updateOrder(orderId, orderUpdateDto);
    }

    // 관리자 : 주문 상태 수정
    @PutMapping("/status/{orderId}")
    public void updateOrderStatus(@PathVariable Long orderId, @RequestBody OrderStatusUpdateDto orderStatusUpdateDto) {
        orderService.updateOrderStatus(orderId, orderStatusUpdateDto);
    }

    // 사용자 : 주문 삭제
    @DeleteMapping("/{orderId}")
    public void deleteOrder(@PathVariable Long orderId) {
        orderService.deleteOrder(orderId);
    }

}
