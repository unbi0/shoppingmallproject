package elice.shoppingmallproject.domain.cart.service;

import java.util.List;
import java.util.stream.Collectors;

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

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final UserUtil userUtil;
    private final UserRepository userRepository;
    private final ProductOptionRepository productOptionRepository;

    public CartResponseDTO addCart(CartCreateDTO cartCreateDTO) {
        Long userId = userUtil.getAuthenticatedUser();
        if (userId == null) {
            throw new IllegalArgumentException("User ID must not be null");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("조회된 유저가 없습니다."));
        ProductOption productOption = productOptionRepository.findById(cartCreateDTO.getOptionId())
                .orElseThrow(() -> new RuntimeException("Product option not found"));
        Product product = productOption.getProduct();
        String url = product.getImages().get(0).getUrl();
        Cart cart = new Cart(productOption, product, user, cartCreateDTO.getQuantity());
        cart = cartRepository.save(cart);
        return toCartResponseDTO(cart);
    }

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

    public CartResponseDTO updateCartItem(Long cartId, int quantity) {
        Long userId = userUtil.getAuthenticatedUser();
        if (userId == null) {
            throw new IllegalArgumentException("User ID must not be null");
        }
        Cart cart = cartRepository.findByCartIdAndUserId(cartId, userId);

        if (cart != null) {
            cart.setQuantity(quantity);
            cart = cartRepository.save(cart);
        } else {
            throw new RuntimeException("Cart item not found or does not belong to the user.");
        }

        return toCartResponseDTO(cart);
    }

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

    public void deleteAllCartItems() {
        Long userId = userUtil.getAuthenticatedUser();
        if (userId == null) {
            throw new IllegalArgumentException("User ID must not be null");
        }
        List<Cart> userCarts = cartRepository.findByUserId(userId);
        cartRepository.deleteAll(userCarts);
    }

    // 총 가격 계산 메서드 추가
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

    private CartResponseDTO toCartResponseDTO(Cart cart) {
        if (cart == null) {
            return null;
        }

        ProductOption productOption = cart.getProductOption(); // 즉시 로딩 사용
        Product product = productOption.getProduct(); // 즉시 로딩 사용

        CartResponseDTO cartResponseDTO = new CartResponseDTO();
        cartResponseDTO.setCartId(cart.getCartId());
        cartResponseDTO.setOptionId(productOption.getOptionId());
        cartResponseDTO.setUserId(cart.getUser().getId());
        cartResponseDTO.setQuantity(cart.getQuantity());
        cartResponseDTO.setProductName(product.getName());
        cartResponseDTO.setProductPrice(product.getPrice());
        cartResponseDTO.setProductImageUrl(product.getImages().get(0).getUrl());
        cartResponseDTO.setProductSize(productOption.getOptionSize()); // 상품 사이즈 추가

        return cartResponseDTO;
    }
}
