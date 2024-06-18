package elice.shoppingmallproject.domain.order.dto;

import elice.shoppingmallproject.domain.order.entity.OrderStatus;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderListDto {

    private Long orderId;
    private LocalDateTime createAt;
    private List<String> productName;
    private OrderStatus orderStatus;

    private String recipientName;
    private String postCode;
    private String deliveryAddress;
    private String deliveryDetailAddress;
    private String recipientTel;
    private String deliveryRequest;

    private int totalCount;
    private int deliveryFee;
    private int totalPrice;
}
