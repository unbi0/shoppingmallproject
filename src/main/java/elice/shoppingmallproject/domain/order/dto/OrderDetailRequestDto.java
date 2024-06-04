package elice.shoppingmallproject.domain.order.dto;

import elice.shoppingmallproject.domain.order.entity.OrderDetail;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailRequestDto {

    // 주문상세 생성 시 필요한 데이터
    private Long orderId;
    private ProductOption productOption;
    private int count;
    private int price;

    public OrderDetail toOrderDetailEntity(){
        return OrderDetail.builder()
            .orderId(this.orderId)
            .productOption(this.productOption)
            .count(this.count)
            .price(this.price)
            .build();
    }
}
