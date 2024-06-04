package elice.shoppingmallproject.domain.order.dto;

import elice.shoppingmallproject.domain.order.entity.OrderDetail;
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
    private Long userId;
    private String deliveryRequest;
    private String recipientName;
    private String recipientTel;
    private String deliveryAddress;
    private String deliveryDetailAddress;
    private int deliveryFee;
    private int totalPrice;
    private List<OrderDetailRequestDto> orderDetailRequestDtoList;

    public Orders toOrdersEntity() {
        return Orders.builder()
            .userId(this.userId)
            .deliveryRequest(this.deliveryRequest)
            .recipientName(this.recipientName)
            .recipientTel(this.recipientTel)
            .deliveryAddress(this.deliveryAddress)
            .deliveryDetailAddress(this.deliveryDetailAddress)
            .deliveryFee(this.deliveryFee)
            .totalPrice(this.totalPrice)
            .build();
    }
}
