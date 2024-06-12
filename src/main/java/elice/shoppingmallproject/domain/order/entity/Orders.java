package elice.shoppingmallproject.domain.order.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import elice.shoppingmallproject.global.common.BaseTimeEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Orders extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @Column(name="user_id")
    private Long userId;

    private String deliveryRequest;
    private String recipientName;
    private String recipientTel;
    private String deliveryAddress;
    private String deliveryDetailAddress;
    private int deliveryFee;
    private int totalPrice;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private OrderStatus orderStatus = OrderStatus.PLACED;

    @OneToMany(mappedBy = "orders", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @JsonIgnore
    private List<OrderDetail> orderDetailList = new ArrayList<>();

    public void updateOrderStatus(OrderStatus newOrderStatus) {
        this.orderStatus = newOrderStatus;
    }

    public Orders updateOrder(String deliveryRequest, String recipientName, String recipientTel, String deliveryAddress, String deliveryDetailAddress, int deliveryFee, int totalPrice){
        this.deliveryRequest = deliveryRequest;
        this.recipientName = recipientName;
        this.recipientTel = recipientTel;
        this.deliveryAddress = deliveryAddress;
        this.deliveryDetailAddress = deliveryDetailAddress;
        this.deliveryFee = deliveryFee;
        this.totalPrice = totalPrice;
        return this;
    }
}
