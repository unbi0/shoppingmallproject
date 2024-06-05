package elice.shoppingmallproject.domain.product.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class ProductController {

    //상품등록 어드민 페이지
    @GetMapping("admin/product/new")
    public String productForm() {
        return "product/productForm";

    }


}