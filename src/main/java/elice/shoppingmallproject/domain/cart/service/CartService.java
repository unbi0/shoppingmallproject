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



    public void save(Cart cart) {
        cartRepository.save(cart);
    }

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