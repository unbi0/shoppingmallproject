package elice.shoppingmallproject.domain.image.controller;

import elice.shoppingmallproject.domain.image.service.S3UploadService;
import elice.shoppingmallproject.domain.product.dto.ProductDTO;
import elice.shoppingmallproject.domain.product.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/productdetail/{productId}")
    public String productDetailsPage(@PathVariable Long productId, Model model) {
        model.addAttribute("productId", productId);
        return "productDetails";
    }

    @GetMapping("product/{productId}")
    public ResponseEntity<ProductDTO> productDetails(@PathVariable("productId") Long productId) {
        ProductDTO productDTO = productService.findById(productId);
        return ResponseEntity.ok(productDTO);
    }

}

