package elice.shoppingmallproject.domain.order.service;

import elice.shoppingmallproject.domain.order.dto.OrderRequestDto;
import elice.shoppingmallproject.domain.order.entity.OrderStatus;
import elice.shoppingmallproject.domain.order.exception.InvalidOrderException;
import elice.shoppingmallproject.domain.order.exception.OrderNotFoundException;
import elice.shoppingmallproject.domain.order.exception.UserNotFoundException;
import elice.shoppingmallproject.domain.order.repository.OrderDetailRepository;
import elice.shoppingmallproject.domain.order.repository.OrderRepository;
import elice.shoppingmallproject.domain.order.entity.OrderDetail;
import elice.shoppingmallproject.domain.order.entity.Orders;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class OrderServiceImpl implements OrderService{

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private UserService userService;

    // 관리자 : 모든 주문내역 조회
    @Override
    public List<Orders> findAllOrders() {
        return orderRepository.findAll();
    }

    // 사용자 : 자신의 모든 주문내역 조회
    @Override
    public List<Orders> findAllUserOrders(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    // 주문번호로 조회
    @Override
    public Optional<Orders> findOrderById(Long id) {
        return orderRepository.findById(id);
    }

    // 주문 날짜 기간으로 조회
    @Override
    public List<Orders> findByCreatedAt(LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.findByCreatedAt(startDate, endDate);
    }

    // 주문상태로 조회
    @Override
    public List<Orders> findByOrderStatus(OrderStatus orderStatus) {
        return orderRepository.findByOrderStatus(orderStatus);
    }

    // 사용자 : 주문 생성
    public Orders createOrder(OrderRequestDto orderRequestDto) {

        // 사용자가 존재하는지 확인
        // spring security 활용


        // 주문 상세 리스트가 비어 있는지 확인
        if (orderRequestDto.getOrderDetails() == null || orderRequestDto.getOrderDetails().isEmpty()) {
            throw new InvalidOrderException("선택된 상품이 없습니다. 상품을 선택해주세요.");
        }

        // 주문 생성
        Orders newOrder = orderRequestDto.toOrdersEntity();

        // 주문 상세 생성
        List<OrderDetail> orderDetails = new ArrayList<>();
        for (OrderDetail orderDetail : orderRequestDto.getOrderDetails()) {
            OrderDetail newOrderDetail = OrderDetail.builder()
                .orders(orderDetail.getOrders())
                .productOption(orderDetail.getProductOption())
                .count(orderDetail.getCount())
                .price(orderDetail.getPrice())
                .build();

            orderDetails.add(newOrderDetail);
        }

        newOrder.setOrderDetailList(orderDetails);
        newOrder = orderRepository.save(newOrder);

        return orderRepository.save(newOrder);
    }

    // 사용자 : 주문 삭제
    @Override
    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }

    // 관리자 : 주문 상태 수정
    // 관리자만 상태 수정할 수 있게 변경 예정
    @Override
    public Orders updateOrderStatus(Long id, String orderStatus) {
        Orders existingOrder = orderRepository.findById(id)
            .orElseThrow(() -> new OrderNotFoundException("주문 ID " + id + "를 찾을 수 없습니다"));

        existingOrder.updateOrderStatus(OrderStatus.valueOf(orderStatus));

        return orderRepository.save(existingOrder);
    }

    // 사용자 : 주문 수정
    @Override
    public Orders updateOrder(Long id, Orders updatedOrders) {
        // 주문이 존재하는지 확인
        Orders existingOrder = orderRepository.findById(id)
            .orElseThrow(() -> new OrderNotFoundException("주문 ID " + id + "를 찾을 수 없습니다"));

        // 수정할 주문 정보
        existingOrder.setDeliveryRequest(updatedOrders.getDeliveryRequest());
        existingOrder.setRecipientName(updatedOrders.getRecipientName());
        existingOrder.setRecipientTel(updatedOrders.getRecipientTel());
        existingOrder.setDeliveryAddress(updatedOrders.getRecipientName());
        existingOrder.setDeliveryDetailAddress(updatedOrders.getRecipientTel());

        return orderRepository.save(existingOrder);
    }
}
