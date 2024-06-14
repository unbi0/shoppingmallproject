package elice.shoppingmallproject.domain.product.service;

import elice.shoppingmallproject.domain.product.dto.ProductDTO;
import elice.shoppingmallproject.domain.product.dto.ProductFormDTO;
import elice.shoppingmallproject.domain.product.dto.ProductOptionDTO;
import elice.shoppingmallproject.domain.category.entity.Category;
import elice.shoppingmallproject.domain.product.entity.Product;
import elice.shoppingmallproject.domain.product.entity.ProductOption;
import elice.shoppingmallproject.domain.category.repository.CategoryRepository;
import elice.shoppingmallproject.domain.product.repository.ProductOptionRepository;
import elice.shoppingmallproject.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;


@Service
@Transactional
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductOptionRepository optionRepository;
    private final CategoryRepository categoryRepository;


    //<관리자 페이지> 상품등록(이미지-추가예정)
    public void addProduct(ProductFormDTO productFormDTO) {

        Category category = categoryRepository.findById(productFormDTO.getCategoryId())
                .orElseThrow(() -> new NoSuchElementException("Category not found for this id: " + productFormDTO.getCategoryId()));

        Product product = Product.createProduct(
                category,
                productFormDTO.getName(),
                productFormDTO.getPrice(),
                productFormDTO.getDescription(),
                productFormDTO.getDetails()
        );
        productRepository.save(product);

        for (ProductOptionDTO optionDTO : productFormDTO.getOptions()) {
            ProductOption productOption = ProductOption.option(
                    optionDTO.getOptionSize(),
                    optionDTO.getStock()
            );
            productOption.setProduct(product);
            optionRepository.save(productOption);
        }
    }



    //<관리자 페이지> 상품 수정 (이미지-추가예정)
    public void updateProduct(Long id, ProductFormDTO productFormDTO) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Product not found for this id: " + id));

        Category category = categoryRepository.findById(productFormDTO.getCategoryId())
                .orElseThrow(() -> new NoSuchElementException("Category not found for this id: " + productFormDTO.getCategoryId()));

        Product.editProduct(
                product,
                category,
                productFormDTO.getName(),
                productFormDTO.getPrice(),
                productFormDTO.getDescription(),
                productFormDTO.getDetails()
        );
        productRepository.save(product);

        optionRepository.deleteByProductId(id);
        for (ProductOptionDTO optionDTO : productFormDTO.getOptions()) {
            ProductOption productOption = ProductOption.option(
                    optionDTO.getOptionSize(),
                    optionDTO.getStock()
            );
            productOption.setProduct(product);
            optionRepository.save(productOption);
        }
    }


    //<관리자 페이지> 상품 삭제
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id).orElse(null); //DB에 삭제할 엔티티 조회 없으면 null
        productRepository.delete(product);
    }





    // 메인 페이지에서 전체 상품 내림차순 조회
    public List<ProductDTO> findAllDesc() {
        List<Product> productList = productRepository.findAllByOrderByProductIdDesc();
        List<ProductDTO> productDTOList = new ArrayList<>();
        for (Product product : productList) {
            ProductDTO productDTO = ProductDTO.toProductDTO(product);
            productDTOList.add(productDTO);
        }
        return productDTOList;
    }


    // 메인 페이지에서 카테고리별 내림차순 조회
    public List<ProductDTO> findAllByCategoryDesc(Long categoryId) {
        List<Product> productList = productRepository.findAllByCategoryIdOrderByProductIdDesc(categoryId);
        List<ProductDTO> productDTOList = new ArrayList<>();
        for (Product product : productList) {
            ProductDTO productDTO = ProductDTO.toProductDTO(product);
            productDTOList.add(productDTO);
        }
        return productDTOList;
    }


    //<메인 페이지> 검색 (이름,제품설명,세푸사항,카테고리에 포함된 단어)
    public List<ProductDTO> searchProducts(String keyword) {
        List<Product> productList = productRepository.searchProducts(keyword);
        List<ProductDTO> productDTOList = new ArrayList<>();

        for (Product product : productList) {
            productDTOList.add(ProductDTO.toProductDTO(product));
        }

        return productDTOList;
    }



    // 상품 단일 조회
    public ProductFormDTO findById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Product not found for this id: " + id));
        return ProductFormDTO.fromProduct(product);
    }



}