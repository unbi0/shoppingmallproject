package elice.shoppingmallproject.domain.order.service;

import elice.shoppingmallproject.domain.order.dto.OrderDetailRequestDto;
import elice.shoppingmallproject.domain.order.dto.OrderDetailUpdateDto;
import elice.shoppingmallproject.domain.order.exception.OrderDetailNotFoundException;
import elice.shoppingmallproject.domain.order.repository.OrderDetailRepository;
import elice.shoppingmallproject.domain.order.entity.OrderDetail;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderDetailServiceImpl implements OrderDetailService{

    private final OrderDetailRepository orderDetailRepository;

    // 모든 주문상세 조회
    @Override
    public List<OrderDetail> findAllOrderDetail(){
        return orderDetailRepository.findAll();
    }

    // 주문 ID로 주문상세 조회
    @Override
    public List<OrderDetail> findOrderDetailByOrderId(Long orderId) {
        return orderDetailRepository.findByOrders_Id(orderId);
    }

    // 사용자 : 주문상세 삭제
    @Override
    public void deleteOrderDetail(Long orderDetailId) {
        orderDetailRepository.deleteById(orderDetailId);
    }

    // 사용자 : 주문상세 수정
    @Override
    public OrderDetail updateOrderDetail(Long id, OrderDetailUpdateDto orderDetailUpdateDto){

        // 주문이 존재하는지 확인
        OrderDetail existingOrderDetail = orderDetailRepository.findById(id)
            .orElseThrow(() -> new OrderDetailNotFoundException("주문상세내역 " + id + "을 찾을 수 없습니다"));

        // 수정할 주문상세 정보
        OrderDetail newOrderDetail = existingOrderDetail.updateOrderDetail(
            orderDetailUpdateDto.getProductOption(),
            orderDetailUpdateDto.getCount(),
            orderDetailUpdateDto.getPrice()
        );
        // 수정한 주문상세 DB 반영
        return orderDetailRepository.save(newOrderDetail);

    }
}
