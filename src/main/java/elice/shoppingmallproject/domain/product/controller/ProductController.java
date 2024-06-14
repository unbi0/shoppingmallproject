package elice.shoppingmallproject.domain.product.controller;

import elice.shoppingmallproject.domain.product.dto.ProductDTO;
import elice.shoppingmallproject.domain.product.dto.ProductFormDTO;
import elice.shoppingmallproject.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;


    //<관리지 페이지> 상품 등록
    @PostMapping("/create")
    public ResponseEntity<String> createProduct(@RequestBody ProductFormDTO productFormDTO) {
        productService.addProduct(productFormDTO);
        return ResponseEntity.ok("CREATE");
    }


    //<관리지 페이지> 수정하기
    @PatchMapping("/{id}")
    public ResponseEntity<String> updateProduct(@PathVariable Long id, @RequestBody ProductFormDTO productFormDTO) {
        productService.updateProduct(id, productFormDTO);
        return ResponseEntity.ok("UPDATE");
    }


    //<관리지 페이지> 삭제하기
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok("DELETE");
    }



    //전체 상품을 내림차순으로 조회
    @GetMapping()
    public ResponseEntity<List<ProductDTO>> findAll() {
        List<ProductDTO> productDTOList = productService.findAllDesc();
        return ResponseEntity.ok(productDTOList);
    }


    //카테고리별 상품 내림차순으로 조회
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductDTO>> findAllByCategoryDesc(@PathVariable Long categoryId) {
        List<ProductDTO> productDTOList = productService.findAllByCategoryDesc(categoryId);
        return ResponseEntity.ok(productDTOList);
    }

    //<메인 페이지> 검색 (이름,제품설명,세부사항,카테고리에 포함된 단어)
    //클라이언트 GET /product/search?keyword=example 요청
    @GetMapping("/search")
    public ResponseEntity<List<ProductDTO>> searchProducts(@RequestParam String keyword) {
        List<ProductDTO> productDTOList = productService.searchProducts(keyword);
        return ResponseEntity.ok(productDTOList);
    }


    //상품 단일 상세페이지
    @GetMapping("/{id}")
    public ResponseEntity<ProductFormDTO> findById(@PathVariable Long id) {
        ProductFormDTO productFormDTO = productService.findById(id);
        return ResponseEntity.ok(productFormDTO);
    }



}


