package elice.shoppingmallproject.domain.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderUpdateDto {

    private String deliveryRequest;
    private String recipientName;
    private String recipientTel;
    private String postCode;
    private String deliveryAddress;
    private String deliveryDetailAddress;
}
