package elice.shoppingmallproject.domain.product.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import elice.shoppingmallproject.domain.product.dto.ProductDTO;
import elice.shoppingmallproject.domain.product.dto.ProductFormDTO;
import elice.shoppingmallproject.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final ObjectMapper objectMapper;

    @PostMapping("/admin/api/product")
    public ResponseEntity<String> createProduct(@RequestParam String productJson,
                                                @RequestParam List<MultipartFile> files) throws IOException {
        ProductFormDTO productFormDTO = objectMapper.readValue(productJson, ProductFormDTO.class);

        productService.addProduct(productFormDTO, files);

        return ResponseEntity.ok("CREATE");
    }

    //<관리지 페이지> 수정하기
    @PatchMapping("/admin/api/product/{id}")
    public ResponseEntity<String> updateProduct(@RequestPart("imgUrl") List<MultipartFile> multipartFiles, @PathVariable Long id,
                                                @RequestBody ProductFormDTO productFormDTO) {
        productService.updateProduct(id, productFormDTO, multipartFiles);
        return ResponseEntity.ok("UPDATE");
    }


    //<관리지 페이지> 삭제하기
    @DeleteMapping("/admin/api/product/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok("DELETE");
    }


    //전체 상품을 내림차순으로 조회
    @GetMapping("/api/product")
    public ResponseEntity<List<ProductDTO>> findAll() {
        List<ProductDTO> productDTOList = productService.findAllDesc();
        return ResponseEntity.ok(productDTOList);
    }


    //카테고리별 상품 내림차순으로 조회
    @GetMapping("/api/product/category/{categoryId}")
    public ResponseEntity<List<ProductDTO>> findAllByCategoryDesc(@PathVariable Long categoryId) {
        List<ProductDTO> productDTOList = productService.findAllByCategoryDesc(categoryId);
        return ResponseEntity.ok(productDTOList);
    }

    //<메인 페이지> 검색 (이름,제품설명,세부사항,카테고리에 포함된 단어)
    @GetMapping("/api/product/search")
    public ResponseEntity<List<ProductDTO>> searchProducts(@RequestParam String keyword) {
        List<ProductDTO> productDTOList = productService.searchProducts(keyword);
        return ResponseEntity.ok(productDTOList);
    }

    //상품 단일 상세페이지 + 관련 이미지 여러개 추가
    @GetMapping("/api/product/{id}")
    public ResponseEntity<ProductDTO> findById(@PathVariable Long id) {
        ProductDTO productDTO = productService.findById(id);
        return ResponseEntity.ok(productDTO);
    }

    @GetMapping("/option/{id}")
    public ResponseEntity<ProductDTO> test(@PathVariable Long id){
        ProductDTO productDTO = productService.findOptionidById(id);
        return ResponseEntity.ok(productDTO);
    }
}