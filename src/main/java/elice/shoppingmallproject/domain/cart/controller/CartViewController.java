package elice.shoppingmallproject.domain.cart.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CartViewController {

    @GetMapping("/cartView")
    public String cart() {
        return "redirect:/carts/cart.html";
    }
}
