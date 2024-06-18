package elice.shoppingmallproject.domain.order.dto;

import elice.shoppingmallproject.domain.order.entity.OrderStatus;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponseDto {

    private OrderStatus orderStatus;
    private String recipientName;
    private LocalDateTime createAt;
    private Long orderId;
    private List<OrderDetailDto> orderDetailDtoList;
    private String postCode;
    private String deliveryAddress;
    private String deliveryDetailAddress;
    private String recipientTel;
    private String deliveryRequest;
    private int totalPrice;
    private int deliveryFee;
    private int totalAmount;
}
