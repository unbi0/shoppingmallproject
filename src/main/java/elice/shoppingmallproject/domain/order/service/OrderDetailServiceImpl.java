package elice.shoppingmallproject.domain.order.service;

import elice.shoppingmallproject.domain.order.entity.Orders;
import elice.shoppingmallproject.domain.order.exception.OrderDetailNotFoundException;
import elice.shoppingmallproject.domain.order.exception.OrderNotFoundException;
import elice.shoppingmallproject.domain.order.repository.OrderDetailRepository;
import elice.shoppingmallproject.domain.order.entity.OrderDetail;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderDetailServiceImpl implements OrderDetailService{

    private final OrderDetailRepository orderDetailRepository;

    @Override
    public List<OrderDetail> findAllOrderDetails() {
        return orderDetailRepository.findAll();
    }

    @Override
    public Optional<OrderDetail> findOrderDetailById(Long id) {
        return orderDetailRepository.findById(id);
    }

    @Override
    public OrderDetail createOrderDetail(OrderDetail orderDetail) {
        return orderDetailRepository.save(orderDetail);
    }

    // 사용자 : 주문상세 삭제
    @Override
    public void deleteOrderDetail(Long id) {
        orderDetailRepository.deleteById(id);
    }

    // 사용자 : 주문상세 수정
    @Override
    public OrderDetail updateOrderdetail(Long id, OrderDetail updatedOrderDetail){

        // 주문이 존재하는지 확인
        OrderDetail existingOrderDetail = orderDetailRepository.findById(id)
            .orElseThrow(() -> new OrderDetailNotFoundException("주문상세내역 " + id + "을 찾을 수 없습니다"));

        // 수정할 주문상세 정보
        OrderDetail newOrderDetail = existingOrderDetail.updateOrderDetail(
            updatedOrderDetail.getProductOption(),
            updatedOrderDetail.getCount(),
            updatedOrderDetail.getPrice()
        );
        // 수정한 주문상세 DB 반영
        return orderDetailRepository.save(newOrderDetail);

    }
}
