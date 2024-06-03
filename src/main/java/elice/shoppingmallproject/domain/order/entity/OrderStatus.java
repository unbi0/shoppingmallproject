package elice.shoppingmallproject.domain.order.entity;

public enum OrderStatus {

    PLACED("주문완료"),
    //    CONFIRMED("주문확인"),
//    PROCESSING("처리중"),
    SHIPPED("배송중"),
    DELIVERED("배송완료");
//    CANCELLED("주문취소"),
//    RETURNED("반품처리"),
//    REFUNDED("환불완료");

    private final String korean;

    OrderStatus(String korean) {
        this.korean = korean;
    }

    public String getKorean() {
        return korean;
    }
}
