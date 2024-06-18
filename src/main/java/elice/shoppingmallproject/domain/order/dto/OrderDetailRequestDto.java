package elice.shoppingmallproject.domain.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailRequestDto {

    // 주문상세 생성 시 필요한 데이터
    private Long productOptionId;
    private int count;
}
