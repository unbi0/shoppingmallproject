package elice.shoppingmallproject.domain.cart.service;

import org.springframework.stereotype.Service;

import elice.shoppingmallproject.domain.cart.dto.CartCreateDTO;
import elice.shoppingmallproject.domain.cart.dto.CartResponseDTO;
import elice.shoppingmallproject.domain.cart.entity.Cart;
import elice.shoppingmallproject.domain.cart.repository.CartRepository;
import elice.shoppingmallproject.domain.order.exception.UserNotFoundException;
import elice.shoppingmallproject.domain.product.entity.Product;
import elice.shoppingmallproject.domain.product.entity.ProductOption;
import elice.shoppingmallproject.domain.product.repository.ProductOptionRepository;
import elice.shoppingmallproject.domain.user.entity.User;
import elice.shoppingmallproject.domain.user.repository.UserRepository;
import elice.shoppingmallproject.global.util.UserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {

    private final CartRepository cartRepository;
    private final UserUtil userUtil;
    private final UserRepository userRepository;
    private final ProductOptionRepository productOptionRepository;

    /**
     * 장바구니에 항목 추가
     */
    public CartResponseDTO addCart(CartCreateDTO cartCreateDTO) {
        Long userId = userUtil.getAuthenticatedUser();
        if (userId == null) {
            throw new IllegalArgumentException("User ID must not be null");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("조회된 유저가 없습니다."));

        ProductOption productOption = productOptionRepository.findById(cartCreateDTO.getOptionId())
                .orElseThrow(() -> {
                    log.error("Product option not found for optionId: {}", cartCreateDTO.getOptionId());
                    return new RuntimeException("Product option not found");
                });

        log.info("Found product option: {}", productOption);

        Product product = productOption.getProduct();
        String imageUrl = product.getImages().get(0).getUrl();

        Cart cart = new Cart(productOption, product, user, cartCreateDTO.getQuantity(), imageUrl);
        cart = cartRepository.save(cart);
        return toCartResponseDTO(cart);
    }

    /**
     * 사용자 ID로 장바구니 항목 조회
     */
    public List<CartResponseDTO> getCartItems() {
        Long userId = userUtil.getAuthenticatedUser();
        if (userId == null) {
            throw new IllegalArgumentException("User ID must not be null");
        }
        List<Cart> cartItems = cartRepository.findByUserId(userId);
        return cartItems.stream()
                .map(this::toCartResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * 장바구니 항목 업데이트
     */
    public CartResponseDTO updateCartItem(Long cartId, int quantity) {
        Long userId = userUtil.getAuthenticatedUser();
        if (userId == null) {
            throw new IllegalArgumentException("User ID must not be null");
        }

        Optional<Cart> optionalCart = cartRepository.findById(cartId);
        if (optionalCart.isPresent()) {
            Cart cart = optionalCart.get();
            if (!cart.getUser().getId().equals(userId)) {
                log.error("Cart item with id {} does not belong to user with id {}", cartId, userId);
                throw new RuntimeException("Cart item not found or does not belong to the user.");
            }
            log.info("Updating cart item: {}, new quantity: {}", cartId, quantity);
            cart.setQuantity(quantity);
            cart = cartRepository.save(cart);
            return toCartResponseDTO(cart);
        } else {
            log.error("Cart item with id {} not found", cartId);
            throw new RuntimeException("Cart item not found or does not belong to the user.");
        }
    }

    /**
     * 장바구니 항목 삭제
     */
    public void deleteCartItem(Long cartId) {
        Long userId = userUtil.getAuthenticatedUser();
        if (userId == null) {
            throw new IllegalArgumentException("User ID must not be null");
        }
        Cart cart = cartRepository.findByCartIdAndUserId(cartId, userId);

        if (cart != null) {
            cartRepository.delete(cart);
        } else {
            throw new RuntimeException("Cart item not found or does not belong to the user.");
        }
    }

    /**
     * 사용자 ID로 모든 장바구니 항목 삭제
     */
    public void deleteAllCartItems() {
        Long userId = userUtil.getAuthenticatedUser();
        if (userId == null) {
            throw new IllegalArgumentException("User ID must not be null");
        }
        List<Cart> userCarts = cartRepository.findByUserId(userId);
        cartRepository.deleteAll(userCarts);
    }

    /**
     * 사용자 ID로 장바구니 항목 조회 및 총 가격 계산
     */
    public double getTotalPrice() {
        Long userId = userUtil.getAuthenticatedUser();
        if (userId == null) {
            throw new IllegalArgumentException("User ID must not be null");
        }
        List<Cart> cartItems = cartRepository.findByUserId(userId);
        return cartItems.stream()
                .mapToDouble(cart -> cart.getProduct().getPrice() * cart.getQuantity())
                .sum();
    }

    /**
     * 사용자 ID와 옵션 ID로 장바구니 항목 조회
     */
    public Cart findByUserIdAndOptionId(Long userId, Long optionId) {
        return cartRepository.findByUserIdAndOptionId(userId, optionId);
    }

    /**
     * 장바구니 항목 저장
     */
    public void save(Cart cart) {
        cartRepository.save(cart);
    }

    /**
     * Cart 엔티티를 CartResponseDTO로 변환
     */
    private CartResponseDTO toCartResponseDTO(Cart cart) {
        if (cart == null) {
            return null;
        }

        ProductOption productOption = cart.getProductOption();
        Product product = productOption.getProduct();

        CartResponseDTO cartResponseDTO = new CartResponseDTO();
        cartResponseDTO.setCartId(cart.getId());
        cartResponseDTO.setOptionId(productOption.getOptionId());
        cartResponseDTO.setUserId(cart.getUser().getId());
        cartResponseDTO.setQuantity(cart.getQuantity());
        cartResponseDTO.setProductName(product.getName());
        cartResponseDTO.setProductPrice(product.getPrice());
        cartResponseDTO.setProductSize(productOption.getOptionSize());
        cartResponseDTO.setImageUrl(product.getImages().get(0).getUrl());
        cartResponseDTO.setProductID(product.getProductId());  // productId 추가

        return cartResponseDTO;
    }
}