package elice.shoppingmallproject.domain.product.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
@Controller
public class ProductViewController {


    @GetMapping("/category/create")
    public String category() {
        return "product/category";
    }


}
