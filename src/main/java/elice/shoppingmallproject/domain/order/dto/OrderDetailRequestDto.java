package elice.shoppingmallproject.domain.order.dto;

import elice.shoppingmallproject.domain.order.entity.OrderDetail;
import elice.shoppingmallproject.domain.order.entity.Orders;
import elice.shoppingmallproject.domain.product.entity.ProductOption;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailRequestDto {

    // 주문상세 생성 시 필요한 데이터
    private ProductOption productOption;
    private int count;
    private int price;

    public OrderDetail toOrderDetailEntity(Orders orders){
        return OrderDetail.builder()
            .orders(orders)
            .productOption(this.productOption)
            .count(this.count)
            .price(this.price)
            .build();
    }
}
