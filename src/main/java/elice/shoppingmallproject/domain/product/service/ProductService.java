package elice.shoppingmallproject.domain.product.service;

import elice.shoppingmallproject.domain.cart.dto.CartCreateDTO;
import elice.shoppingmallproject.domain.image.entity.Image;
import elice.shoppingmallproject.domain.image.repository.ImageRepository;
import elice.shoppingmallproject.domain.image.service.S3UploadService;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;


@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductOptionRepository optionRepository;
    private final CategoryRepository categoryRepository;
    private final S3UploadService s3UploadService;
    private final ImageRepository imageRepository;


    //<관리자 페이지> 상품등록
    public void addProduct(ProductFormDTO productFormDTO, List<MultipartFile> files) {

        Category category = categoryRepository.findById(productFormDTO.getCategoryId())
                .orElseThrow(() -> new NoSuchElementException("Category not found for this id: " + productFormDTO.getCategoryId()));

        List<ProductOption> productOptions = productFormDTO.getOptions().stream()
                .map(optionDTO -> {
                    ProductOption option = new ProductOption();
                    option.setOptionSize(optionDTO.getOptionSize());
                    option.setStock(optionDTO.getStock());
                    return option;
                }).toList();

        List<Image> images = s3UploadService.uploadFiles(files);

        Product product = Product.createProduct(
                category,
                productFormDTO.getName(),
                productFormDTO.getPrice(),
                productFormDTO.getDescription(),
                productFormDTO.getDetails(),
                productOptions,
                images
        );
        productRepository.save(product);
    }
    
    //<관리자 페이지> 상품 수정
    public void updateProduct(Long id, ProductFormDTO productFormDTO, List<MultipartFile> multipartFile) {

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
            product.addOption(productOption);
            optionRepository.save(productOption);
        }

        if (multipartFile != null) {
            List<Image> uploadedImages = s3UploadService.uploadFiles(multipartFile);

            for (Image file : uploadedImages) {
                product.addImage(file);
                imageRepository.save(file);
            }
        }
    }

    //<관리자 페이지> 상품 삭제
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Product not found for this id: " + id));

        // 연관된 이미지 DB와 S3에서 삭제
        List<Image> images = product.getImages();
        Iterator<Image> imageIterator = images.iterator();

        while (imageIterator.hasNext()) {
            Image image = imageIterator.next();
            imageIterator.remove();  // 컬렉션에서 안전하게 제거
            s3UploadService.deleteImage(image.getImage_id());
            imageRepository.delete(image);
        }

        // 연관된 옵션 삭제
        List<ProductOption> options = product.getOptions();
        if (options.isEmpty()) {
            throw new NoSuchElementException("Product options not found for this product id: " + id);
        }

        Iterator<ProductOption> optionIterator = options.iterator();

        while (optionIterator.hasNext()) {
            ProductOption option = optionIterator.next();
            optionIterator.remove();  // 컬렉션에서 안전하게 제거
            optionRepository.delete(option);
        }

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
    public ProductDTO findById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Product with id " + id + " not found"));
        return ProductDTO.toProductDTO(product);
    }

    public ProductDTO findOptionidById(Long id) {
        Product product = productRepository.findOptionidById(id);
        return ProductDTO.toProductDTO(product);
    }
}
