package elice.shoppingmallproject.domain.product.controller;

import elice.shoppingmallproject.domain.product.dto.ProductDTO;
import elice.shoppingmallproject.domain.product.dto.ProductFormDTO;
import elice.shoppingmallproject.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;


    // 상품 등록
    @PostMapping("/create")
    public ResponseEntity<String> createProduct(@RequestBody ProductFormDTO productFormDTO) {
        productService.addProduct(productFormDTO);
        return ResponseEntity.ok("CREATE");
    }


    // 판매 리스트
    @GetMapping()
    public ResponseEntity<List<ProductDTO>> findAll() {
        List<ProductDTO> productDTOList = productService.findAll();
        return ResponseEntity.ok(productDTOList);
    }
}



