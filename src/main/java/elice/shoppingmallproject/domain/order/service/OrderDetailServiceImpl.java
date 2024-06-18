package elice.shoppingmallproject.domain.order.service;

import elice.shoppingmallproject.domain.order.repository.OrderDetailRepository;
import elice.shoppingmallproject.domain.order.entity.OrderDetail;
import jakarta.transaction.Transactional;
import java.util.List;
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
}
