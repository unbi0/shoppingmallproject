package elice.shoppingmallproject.domain.order.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class OrderViewController {

    // 주문서 작성 페이지
    @GetMapping("/order")
    public String orderView() {
        return "order/order";
    }

    // 사용자 주문 관리 페이지
    @GetMapping("/user/order")
    public String userOrderView() {
        return "order/order-user";
    }

    // 관리자 주문 관리 페이지
    @GetMapping("/admin/order")
    public String adminOrderView() {
        return "order/order-admin";
    }

    // 주문 수정 페이지
    @GetMapping("/order/{orderId}/edit")
    public String orderEditView() {
        return "order/order-edit";
    }

    // 주문 상세 페이지
    @GetMapping("/order/{orderId}")
    public String orderDetailView() {
        return "order/order-detail";
    }

}
