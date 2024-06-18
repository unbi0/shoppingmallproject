package elice.shoppingmallproject.domain.cart.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartResponseDTO {
    private Long cartId;
    private Long optionId;
    private Long userId;
    private int quantity;
    private String productName;  // 상품 이름 추가
    private int productPrice;    // 상품 가격 추가
    private String productImageUrl; // 상품 이미지 URL 추가
    private String productSize; // 상품 사이즈 추가
}