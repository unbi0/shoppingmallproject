package elice.shoppingmallproject.domain.order.dto;

import elice.shoppingmallproject.domain.order.entity.Orders;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestDto {

    // 주문 생성 시 필요한 데이터
    private String deliveryRequest;
    private String recipientName;
    private String recipientTel;
    private String deliveryAddress;
    private String deliveryDetailAddress;
    private int deliveryFee;
    private List<OrderDetailRequestDto> orderDetailRequestDtoList;
}
