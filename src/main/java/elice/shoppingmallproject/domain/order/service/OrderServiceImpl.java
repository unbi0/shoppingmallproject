package elice.shoppingmallproject.domain.order.service;

import elice.shoppingmallproject.domain.order.dto.OrderDetailRequestDto;
import elice.shoppingmallproject.domain.order.dto.OrderRequestDto;
import elice.shoppingmallproject.domain.order.dto.OrderUpdateDto;
import elice.shoppingmallproject.domain.order.entity.OrderDetail;
import elice.shoppingmallproject.domain.order.entity.OrderStatus;
import elice.shoppingmallproject.domain.order.exception.InvalidOrderException;
import elice.shoppingmallproject.domain.order.exception.OrderNotFoundException;
import elice.shoppingmallproject.domain.order.repository.OrderDetailRepository;
import elice.shoppingmallproject.domain.order.repository.OrderRepository;
import elice.shoppingmallproject.domain.order.entity.Orders;
import elice.shoppingmallproject.global.util.UserUtil;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService{

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final UserUtil userUtil;

    // 관리자 : 주문 조회
    @Override
    public List<Orders> searchAllOrders(Long orderId, LocalDateTime startDate, LocalDateTime endDate, OrderStatus orderStatus) {
        return orderRepository.searchAllOrders(orderId, startDate, endDate, orderStatus);
    }

    // 사용자 : 주문 조회
    @Override
    public List<Orders> searchUserOrders(Long orderId, LocalDateTime startDate, LocalDateTime endDate, OrderStatus orderStatus) {
        Long userId = userUtil.getAuthenticatedUser();
        return orderRepository.searchUserOrders(userId, orderId, startDate, endDate, orderStatus);
    }

    // 사용자 : 주문 생성
    public Orders createOrder(OrderRequestDto orderRequestDto) {

        // 주문 상세 리스트가 비어 있는지 확인
        if (orderRequestDto.getOrderDetailRequestDtoList() == null || orderRequestDto.getOrderDetailRequestDtoList().isEmpty()) {
            throw new InvalidOrderException("선택된 상품이 없습니다. 상품을 선택해주세요.");
        }

        int totalPrice = 0;
        // 각각의 주문상세에서 수량과 가격 정보를 가져와서 totalPrice 계산
        for (OrderDetailRequestDto orderDetailRequestDto : orderRequestDto.getOrderDetailRequestDtoList()) {
            // 상품 가격
            int productPrice = orderDetailRequestDto.getProductOption().getProduct().getPrice();
            // 상품 수량
            int quantity = orderDetailRequestDto.getCount();

            // 상품 가격 x 상품 수량의 총합
            totalPrice += (productPrice * quantity);
        }

        // 주문 생성
        Orders newOrder = Orders.builder()
            .userId(userUtil.getAuthenticatedUser())
            .deliveryRequest(orderRequestDto.getDeliveryRequest())
            .recipientName(orderRequestDto.getRecipientName())
            .recipientTel(orderRequestDto.getRecipientTel())
            .deliveryAddress(orderRequestDto.getDeliveryAddress())
            .deliveryDetailAddress(orderRequestDto.getDeliveryDetailAddress())
            .deliveryFee(orderRequestDto.getDeliveryFee())
            .totalPrice(totalPrice)
            .build();

        // 주문 상세 생성
        List<OrderDetail> orderDetails = new ArrayList<>();
        for (OrderDetailRequestDto orderDetailRequestDto : orderRequestDto.getOrderDetailRequestDtoList()) {

            // productOption -> Product 에서 가격 정보 가져와서 세팅
            int price = orderDetailRequestDto.getProductOption().getProduct().getPrice();

            OrderDetail newOrderDetail = OrderDetail.builder()
                .orders(newOrder)
                .productOption(orderDetailRequestDto.getProductOption())
                .count(orderDetailRequestDto.getCount())
                .price(price)
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
    public Orders updateOrder(Long id, OrderUpdateDto orderUpdateDto) {
        // 주문이 존재하는지 확인
        Orders existingOrder = orderRepository.findById(id)
            .orElseThrow(() -> new OrderNotFoundException("주문 ID " + id + "를 찾을 수 없습니다"));

        // 수정할 주문 정보
        Orders newOrders = existingOrder.updateOrder(
            orderUpdateDto.getDeliveryRequest(),
            orderUpdateDto.getRecipientName(),
            orderUpdateDto.getRecipientTel(),
            orderUpdateDto.getDeliveryAddress(),
            orderUpdateDto.getDeliveryDetailAddress()
        );

        // 수정한 내용 DB 반영
        return orderRepository.save(newOrders);
    }
}
