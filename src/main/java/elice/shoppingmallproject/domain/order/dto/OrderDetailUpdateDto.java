package elice.shoppingmallproject.domain.order.dto;

import elice.shoppingmallproject.domain.product.entity.ProductOption;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailUpdateDto {

    // 주문상세 수정 시 필요한 데이터
    private ProductOption productOption;
    private int count;
}
