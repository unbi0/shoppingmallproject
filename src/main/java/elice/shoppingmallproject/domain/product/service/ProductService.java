package elice.shoppingmallproject.domain.product.service;

import elice.shoppingmallproject.domain.product.dto.ProductDTO;
import elice.shoppingmallproject.domain.product.dto.ProductFormDTO;
import elice.shoppingmallproject.domain.product.dto.ProductOptionDTO;
import elice.shoppingmallproject.domain.product.entity.Product;
import elice.shoppingmallproject.domain.product.entity.ProductOption;
import elice.shoppingmallproject.domain.product.repository.ProductOptionRepository;
import elice.shoppingmallproject.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductOptionRepository optionRepository;


    public void addProduct(ProductFormDTO productFormDTO) {
        Product product = Product.createProduct(productFormDTO.getName(),
                productFormDTO.getPrice(),
                productFormDTO.getDescription(),
                productFormDTO.getDetails());
        productRepository.save(product);


        for (ProductOptionDTO optionDTO : productFormDTO.getOptions()) {
            ProductOption productOption = ProductOption.option(
                    optionDTO.getOptionSize(),
                    optionDTO.getStock()
            );
            productOption.setProduct(product);
            optionRepository.save(productOption);


            //이미지 기능 완성시 추가

        }
    }

    //상품 목록 리스트 로직
    public List<ProductDTO> findAll() {
        List<Product> productList = productRepository.findAll();
        List<ProductDTO> productDTOList = new ArrayList<>();
        for (Product product: productList) {
            productDTOList.add(ProductDTO.toProductDTO(product));

        }
        return productDTOList;
    }


    /*public void editProduct(Long productId, ProductFormDTO productFormDTO) {
        Optional<Product> optionalProduct = productRepository.findById(productId);
            Product product = optionalProduct.get();
            product.setName(productFormDTO.getName());
            product.setPrice(productFormDTO.getPrice());
            product.setDescription(productFormDTO.getDescription());
            product.setDetails(productFormDTO.getDetails());


    }
*/


}