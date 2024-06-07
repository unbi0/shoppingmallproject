package elice.shoppingmallproject.domain.order.entity;

import elice.shoppingmallproject.global.common.BaseTimeEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.query.Order;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Orders extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

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
    private OrderStatus orderStatus = OrderStatus.PLACED;

    @OneToMany(mappedBy = "orders", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
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
