package elice.shoppingmallproject.domain.cart.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartResponseDTO {
    private Long cartId; // 장바구니 항목 ID
    private Long optionId; // 상품 옵션 ID
    private Long userId; // 사용자 ID
    private int quantity; // 수량
    private String productName; // 상품 이름
    private int productPrice; // 상품 가격
    private String imageUrl; // 상품 이미지 URL
    private String productSize; // 상품 사이즈
    private Long productID; // 상품 ID 추가
}