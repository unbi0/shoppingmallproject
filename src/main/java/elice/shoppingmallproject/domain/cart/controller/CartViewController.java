package elice.shoppingmallproject.domain.cart.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CartViewController {
    @GetMapping("/cart/view")
    public String viewCart() {
        return "carts/cart";
    }
}
