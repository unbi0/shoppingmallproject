package elice.shoppingmallproject.domain.cart.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartCreateDTO {
    private Long optionId;
    private Long userId;
    private int quantity;
}
