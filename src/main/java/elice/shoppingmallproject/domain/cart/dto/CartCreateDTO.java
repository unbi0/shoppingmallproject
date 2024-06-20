package elice.shoppingmallproject.domain.cart.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartCreateDTO {

    private Long optionId;
    private int quantity;

    // 롬복의 @NoArgsConstructor, @AllArgsConstructor로 생성자 자동 생성됨

}

