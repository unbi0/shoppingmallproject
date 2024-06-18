package elice.shoppingmallproject.domain.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailDto {

    private Long productId;
    private String productName;
    private int price;
    private String optionSize;
    private int count;
}
