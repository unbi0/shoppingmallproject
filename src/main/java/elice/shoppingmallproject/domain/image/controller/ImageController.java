package elice.shoppingmallproject.domain.image.controller;

import elice.shoppingmallproject.domain.image.service.S3UploadService;
import elice.shoppingmallproject.domain.product.dto.ProductDTO;
import elice.shoppingmallproject.domain.product.entity.Product;
import elice.shoppingmallproject.domain.product.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@Slf4j
public class ImageController {

    private final S3UploadService s3UploadService;
    private final ProductService productService;

    @Autowired
    public ImageController(S3UploadService s3UploadService, ProductService productService) {
        this.s3UploadService = s3UploadService;
        this.productService = productService;
    }

    @GetMapping("/product/store")
    public String inputProduct() {
        // 필요한 경우 모델에 데이터 추가
        return "inputProduct";
    }

    @GetMapping("/product/details")
    public String productDetails() {
        // 필요한 경우 모델에 데이터 추가
        return "productDetails";
    }

    @GetMapping("/product/details/{productId}")
    public String productDetails(@PathVariable("productId") Long productId, Model model) {
        // 제품 ID를 이용해 제품 정보를 가져옴 (예: 데이터베이스에서 조회)
        ProductDTO product = productService.findById(productId);

        // 모델에 데이터 추가
        model.addAttribute("productId", product.getProductId());
        model.addAttribute("productName", product.getName());
        model.addAttribute("productImageUrl", product.getImageUrl());
        model.addAttribute("productPrice", product.getPrice());

        return "productDetails";
    }


}

