package elice.shoppingmallproject.domain.order.service;

import elice.shoppingmallproject.domain.order.dto.OrderDetailRequestDto;
import elice.shoppingmallproject.domain.order.dto.OrderRequestDto;
import elice.shoppingmallproject.domain.order.entity.OrderDetail;
import elice.shoppingmallproject.domain.order.entity.OrderStatus;
import elice.shoppingmallproject.domain.order.exception.InvalidOrderException;
import elice.shoppingmallproject.domain.order.exception.OrderNotFoundException;
import elice.shoppingmallproject.domain.order.repository.OrderDetailRepository;
import elice.shoppingmallproject.domain.order.repository.OrderRepository;
import elice.shoppingmallproject.domain.order.entity.Orders;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService{

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
//    private final UserUtil userUtil;

    // 관리자 : 주문 조회
    @Override
    public List<Orders> searchAllOrders(Long orderId, LocalDateTime startDate, LocalDateTime endDate, OrderStatus orderStatus) {
        return orderRepository.searchAllOrders(orderId, startDate, endDate, orderStatus);
    }

    // 사용자 : 주문 조회
    @Override
    public List<Orders> searchUserOrders(Long userId, Long orderId, LocalDateTime startDate, LocalDateTime endDate, OrderStatus orderStatus) {
//        Long userId = userUtil.getAuthenticatedUser();
        return orderRepository.searchUserOrders(userId, orderId, startDate, endDate, orderStatus);
    }

    // 사용자 : 주문 생성
    public Orders createOrder(OrderRequestDto orderRequestDto) {

        // 주문 상세 리스트가 비어 있는지 확인
        if (orderRequestDto.getOrderDetailRequestDtoList() == null || orderRequestDto.getOrderDetailRequestDtoList().isEmpty()) {
            throw new InvalidOrderException("선택된 상품이 없습니다. 상품을 선택해주세요.");
        }

        // 주문 생성
        Orders newOrder = orderRequestDto.toOrdersEntity();
        newOrder = orderRepository.save(newOrder);

        // 주문 상세 생성
        List<OrderDetail> orderDetails = new ArrayList<>();
        for (OrderDetailRequestDto orderDetailRequestDto : orderRequestDto.getOrderDetailRequestDtoList()) {
            OrderDetail newOrderDetail = OrderDetail.builder()
                .orders(newOrder)
                .productOption(orderDetailRequestDto.getProductOption())
                .count(orderDetailRequestDto.getCount())
                .price(orderDetailRequestDto.getPrice())
                .build();

            orderDetails.add(newOrderDetail);
        }
        orderDetailRepository.saveAll(orderDetails);

        return orderRepository.save(newOrder);
    }

    // 사용자 : 주문 삭제
    @Override
    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }

    // 관리자 : 주문 상태 수정
    @Override
    public Orders updateOrderStatus(Long id, OrderStatus newOrderStatus) {
        // 주문이 존재하는지 확인
        Orders existingOrder = orderRepository.findById(id)
            .orElseThrow(() -> new OrderNotFoundException("주문 ID " + id + "를 찾을 수 없습니다"));

        existingOrder.updateOrderStatus(newOrderStatus);

        return orderRepository.save(existingOrder);
    }

    // 사용자 : 주문 수정
    @Override
    public Orders updateOrder(Long id, Orders updatedOrders) {
        // 주문이 존재하는지 확인
        Orders existingOrder = orderRepository.findById(id)
            .orElseThrow(() -> new OrderNotFoundException("주문 ID " + id + "를 찾을 수 없습니다"));

        // 수정할 주문 정보
        Orders newOrders = existingOrder.updateOrder(
            updatedOrders.getDeliveryRequest(),
            updatedOrders.getRecipientName(),
            updatedOrders.getRecipientTel(),
            updatedOrders.getDeliveryAddress(),
            updatedOrders.getDeliveryDetailAddress(),
            updatedOrders.getDeliveryFee(),
            updatedOrders.getTotalPrice()
        );

        // 수정한 내용 DB 반영
        return orderRepository.save(newOrders);
    }
}
