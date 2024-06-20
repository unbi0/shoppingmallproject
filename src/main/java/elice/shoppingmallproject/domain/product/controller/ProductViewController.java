package elice.shoppingmallproject.domain.product.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ProductViewController {


    @GetMapping("/admin/category/create")
    public String category() {
        return "product/category";
    }

    @GetMapping("/admin/product/store")
    public String inputProduct() {
        // 필요한 경우 모델에 데이터 추가
        return "inputProduct";
    }

    @GetMapping("/productdetail/{productId}")
    public String productDetailsPage(@PathVariable Long productId) {
        //model.addAttribute("productId", productId);
        return "productDetails";
    }

}
