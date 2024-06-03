package elice.shoppingmallproject.domain.product.service;

import elice.shoppingmallproject.domain.product.dto.ProductFormDTO;
import elice.shoppingmallproject.domain.product.entity.Product;
import elice.shoppingmallproject.domain.product.entity.ProductOption;
import elice.shoppingmallproject.domain.product.repository.ProductOptionRepository;
import elice.shoppingmallproject.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    //private final ImageService imgService;
    //private final ImageRepository imageRepository;
    private final ProductOptionRepository optionRepository;


    public void addProduct(ProductFormDTO productFormDTO) {
        Product product = new Product();
        product.setName(productFormDTO.getName());
        product.setDescription(productFormDTO.getDescription());
        product.setPrice(productFormDTO.getPrice());
        product.setDetails(productFormDTO.getDetails());
        productRepository.save(product);


        ProductOption productOption = new ProductOption();
        productOption.setStock(productFormDTO.getStock());
        productOption.setOptionSize(productFormDTO.getOptionSize());
        productOption.setProduct(product);

        optionRepository.save(productOption);

        // 이미지 리스트를 ImageEntity로 변환하여 설정
        List<ImageEntity> images = productFormDTO.getImages().stream()
                .map(imageDto -> {
                    ImageEntity imageEntity = imageDto.toEntity();
                    imageEntity.setProduct(product); // 관계 설정
                    return imageEntity;
                })
                .collect(Collectors.toList());
        product.setImages(images);

        ProductRepository.save(product);
    }
}