package elice.shoppingmallproject.domain.order.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class OrderViewController {

    // 주문서 작성 페이지
    @GetMapping("/order")
    public String orderView() {
        return "order";
    }

    // 사용자 주문 관리 페이지
    @GetMapping("/order-user")
    public String userOrderView() {
        return "order-user";
    }

    // 관리자 주문 관리 페이지
    @GetMapping("/order-admin")
    public String adminOrderView() {
        return "order-admin";
    }

    // 주문 수정 페이지
    @GetMapping("/order-edit")
    public String orderEditView() {
        return "order-edit";
    }

    // 주문 상세 페이지
    @GetMapping("/order/{orderId}")
    public String orderDetailView() {
        return "order-detail";
    }

}
