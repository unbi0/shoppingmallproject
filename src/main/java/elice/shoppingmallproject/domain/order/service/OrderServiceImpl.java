package elice.shoppingmallproject.domain.order.service;

import elice.shoppingmallproject.domain.cart.service.CartService;
import elice.shoppingmallproject.domain.order.dto.OrderDetailDto;
import elice.shoppingmallproject.domain.order.dto.OrderDetailRequestDto;
import elice.shoppingmallproject.domain.order.dto.OrderListDto;
import elice.shoppingmallproject.domain.order.dto.OrderRequestDto;
import elice.shoppingmallproject.domain.order.dto.OrderResponseDto;
import elice.shoppingmallproject.domain.order.dto.OrderStatusUpdateDto;
import elice.shoppingmallproject.domain.order.dto.OrderUpdateDto;
import elice.shoppingmallproject.domain.order.entity.OrderDetail;
import elice.shoppingmallproject.domain.order.entity.OrderStatus;
import elice.shoppingmallproject.domain.order.exception.InvalidOrderException;
import elice.shoppingmallproject.domain.order.exception.OrderNotFoundException;
import elice.shoppingmallproject.domain.order.repository.OrderDetailRepository;
import elice.shoppingmallproject.domain.order.repository.OrderRepository;
import elice.shoppingmallproject.domain.order.entity.Orders;
import elice.shoppingmallproject.domain.product.entity.ProductOption;
import elice.shoppingmallproject.domain.product.repository.ProductOptionRepository;
import elice.shoppingmallproject.global.util.UserUtil;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService{

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ProductOptionRepository productOptionRepository;
    private final CartService cartService;
    private final UserUtil userUtil;

    // 관리자 : 주문 조회
    @Override
    public Page<OrderListDto> searchAllOrders(Long orderId, LocalDateTime startDate, LocalDateTime endDate, OrderStatus orderStatus, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        if (endDate != null) {
            endDate = endDate.plusDays(1);
        }
        Page<Orders> ordersPage = orderRepository.searchAllOrders(orderId, startDate, endDate, orderStatus, pageable);
        return ordersPage.map(this::mapToOrderListDto);
    }

    @Override
    public Page<OrderListDto> searchUserOrders(Long orderId, LocalDateTime startDate, LocalDateTime endDate, int page, int size) {
        Long userId = userUtil.getAuthenticatedUser();
        if (endDate != null) {
            endDate = endDate.plusDays(1);
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<Orders> ordersPage = orderRepository.searchUserOrders(userId, orderId, startDate, endDate, pageable);
        return ordersPage.map(this::mapToOrderListDto);
    }

    // Orders 엔티티를 OrderListDto로 매핑하는 메서드
    private OrderListDto mapToOrderListDto(Orders order) {
        return new OrderListDto(
            order.getId(),
            order.getCreateAt(),
            order.getOrderDetailList().stream()
                .map(orderDetail -> orderDetail.getProductOption().getProduct().getName())
                .collect(Collectors.toList()),
            order.getOrderStatus(),
            order.getRecipientName(),
            order.getPostCode(),
            order.getDeliveryAddress(),
            order.getDeliveryDetailAddress(),
            order.getRecipientTel(),
            order.getDeliveryRequest(),
            order.getOrderDetailList().stream()
                .mapToInt(OrderDetail::getCount)
                .sum(),
            order.getDeliveryFee(),
            order.getTotalPrice()
        );
    }

    // 주문 ID로 주문 조회
    @Override
    public Optional<OrderResponseDto> findOrderById(Long orderId) {
        return orderRepository.findById(orderId)
            .map(order -> {
                List<OrderDetailDto> orderDetailDtoList = order.getOrderDetailList().stream()
                    .map(orderDetail -> new OrderDetailDto(
                        orderDetail.getProductOption().getProduct().getProductId(),
                        orderDetail.getProductOption().getProduct().getName(),
                        orderDetail.getPrice(),
                        orderDetail.getProductOption().getOptionSize(),
                        orderDetail.getCount(),
                        orderDetail.getProductOption().getProduct().getImages().get(0).getUrl()
                    ))
                    .collect(Collectors.toList());

                int totalPrice = order.getOrderDetailList().stream()
                    .mapToInt(orderDetail -> orderDetail.getCount() * orderDetail.getPrice())
                    .sum();

                return OrderResponseDto.builder()
                    .orderStatus(order.getOrderStatus())
                    .recipientName(order.getRecipientName())
                    .createAt(order.getCreateAt())
                    .orderId(order.getId())
                    .orderDetailDtoList(orderDetailDtoList)
                    .postCode(order.getPostCode())
                    .deliveryAddress(order.getDeliveryAddress())
                    .deliveryDetailAddress(order.getDeliveryDetailAddress())
                    .recipientTel(order.getRecipientTel())
                    .deliveryRequest(order.getDeliveryRequest())
                    .totalPrice(totalPrice)
                    .deliveryFee(order.getDeliveryFee())
                    .totalAmount(totalPrice + order.getDeliveryFee())
                    .build();
            });
    }

    // 사용자 : 주문 생성
    @Transactional
    public Orders createOrder(OrderRequestDto orderRequestDto) {

        // 주문 상세 리스트가 비어 있는지 확인
        if (orderRequestDto.getOrderDetailRequestDtoList() == null || orderRequestDto.getOrderDetailRequestDtoList().isEmpty()) {
            throw new InvalidOrderException("선택된 상품이 없습니다. 상품을 선택해주세요.");
        }

        int totalPrice = 0;
        // 각각의 주문상세에서 수량과 가격 정보를 가져와서 totalPrice 계산
        for (OrderDetailRequestDto orderDetailRequestDto : orderRequestDto.getOrderDetailRequestDtoList()) {
            ProductOption productOption = productOptionRepository.findById(orderDetailRequestDto.getProductOptionId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid product option ID: " + orderDetailRequestDto.getProductOptionId()));
            // 상품 가격
            int productPrice = productOption.getProduct().getPrice();
            // 상품 수량
            int quantity = orderDetailRequestDto.getCount();

            // 재고 감소 기능
            productOption.decreaseStock(quantity);

            // 상품 가격 x 상품 수량의 총합
            totalPrice += (productPrice * quantity);
        }

        // 주문 생성
        Orders newOrder = Orders.builder()
            .userId(userUtil.getAuthenticatedUser())
            .deliveryRequest(orderRequestDto.getDeliveryRequest())
            .recipientName(orderRequestDto.getRecipientName())
            .recipientTel(orderRequestDto.getRecipientTel())
            .postCode((orderRequestDto.getPostCode()))
            .deliveryAddress(orderRequestDto.getDeliveryAddress())
            .deliveryDetailAddress(orderRequestDto.getDeliveryDetailAddress())
            .deliveryFee(orderRequestDto.getDeliveryFee())
            .totalPrice(totalPrice)
            .build();

        // 주문 상세 생성
        List<OrderDetail> orderDetails = new ArrayList<>();
        for (OrderDetailRequestDto orderDetailRequestDto : orderRequestDto.getOrderDetailRequestDtoList()) {

            ProductOption productOption = productOptionRepository.findById(orderDetailRequestDto.getProductOptionId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid product option ID: " + orderDetailRequestDto.getProductOptionId()));

            int price = productOption.getProduct().getPrice();

            OrderDetail newOrderDetail = OrderDetail.builder()
                .orders(newOrder)
                .productOption(productOption)
                .count(orderDetailRequestDto.getCount())
                .price(price)
                .build();

            orderDetails.add(newOrderDetail);
        }
        orderDetailRepository.saveAll(orderDetails);

        // 장바구니 비우기
//        cartService.deleteAllCartItems();

        return orderRepository.save(newOrder);
    }

    // 사용자 : 주문 삭제
    @Override
    public void deleteOrder(Long id) {

        Orders existingOrder = orderRepository.findById(id)
            .orElseThrow(() -> new OrderNotFoundException("주문 ID " + id + "를 찾을 수 없습니다"));

        for (OrderDetail orderDetail : existingOrder.getOrderDetailList()){
            orderDetail.getProductOption().increaseStock(orderDetail.getCount());
        }
        orderRepository.deleteById(id);
    }

    // 관리자 : 주문 상태 수정
    @Override
    public void updateOrderStatus(Long id, OrderStatusUpdateDto orderStatusUpdateDto) {

        OrderStatus newStatus = orderStatusUpdateDto.getOrderStatus();
        // 주문이 존재하는지 확인
        Orders existingOrder = orderRepository.findById(id)
            .orElseThrow(() -> new OrderNotFoundException("주문 ID " + id + "를 찾을 수 없습니다"));

        existingOrder.updateOrderStatus(newStatus);



        orderRepository.save(existingOrder);
    }

    // 사용자 : 주문 수정
    @Override
    public void updateOrder(Long id, OrderUpdateDto orderUpdateDto) {
        // 주문이 존재하는지 확인
        Orders existingOrder = orderRepository.findById(id)
            .orElseThrow(() -> new OrderNotFoundException("주문 ID " + id + "를 찾을 수 없습니다"));

        // 수정할 주문 정보
        Orders newOrders = existingOrder.updateOrder(
            orderUpdateDto.getDeliveryRequest(),
            orderUpdateDto.getRecipientName(),
            orderUpdateDto.getRecipientTel(),
            orderUpdateDto.getPostCode(),
            orderUpdateDto.getDeliveryAddress(),
            orderUpdateDto.getDeliveryDetailAddress()
        );

        // 수정한 내용 DB 반영
        orderRepository.save(newOrders);
    }
}
