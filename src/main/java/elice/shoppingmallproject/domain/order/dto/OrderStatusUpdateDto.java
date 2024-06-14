package elice.shoppingmallproject.domain.order.dto;

import elice.shoppingmallproject.domain.order.entity.OrderStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrderStatusUpdateDto {

    private OrderStatus orderStatus;
}