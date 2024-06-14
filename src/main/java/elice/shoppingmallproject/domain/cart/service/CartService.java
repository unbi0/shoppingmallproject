package elice.shoppingmallproject.domain.cart.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import elice.shoppingmallproject.domain.cart.dto.CartCreateDTO;
import elice.shoppingmallproject.domain.cart.dto.CartResponseDTO;
import elice.shoppingmallproject.domain.cart.entity.Cart;
import elice.shoppingmallproject.domain.cart.repository.CartRepository;
import elice.shoppingmallproject.domain.order.exception.UserNotFoundException;
import elice.shoppingmallproject.domain.user.entity.User;
import elice.shoppingmallproject.domain.user.repository.UserRepository;
import elice.shoppingmallproject.global.util.UserUtil;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final UserUtil userUtil;
    private final UserRepository userRepository;

    public CartService(CartRepository cartRepository, UserUtil userUtil, UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.userUtil = userUtil;
        this.userRepository = userRepository;
    }

    public CartResponseDTO addCart(CartCreateDTO cartCreateDTO) {
        Long userId = userUtil.getAuthenticatedUser();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("조회된 유저가 없습니다."));
        Cart cart = new Cart(cartCreateDTO.getOptionId(), user, cartCreateDTO.getQuantity());
        cart = cartRepository.save(cart);
        return toCartResponseDTO(cart);
    }

    public List<CartResponseDTO> getCartItems() {
        Long userId = userUtil.getAuthenticatedUser();
        List<Cart> cartItems = cartRepository.findByUserId(userId);
        return cartItems.stream()
                .map(this::toCartResponseDTO)
                .collect(Collectors.toList());
    }

    public CartResponseDTO updateCartItem(Long cartId, int quantity) {
        Long userId = userUtil.getAuthenticatedUser();
        Cart cart = cartRepository.findById(cartId).orElse(null);
        if (cart != null && cart.getUser().getId().equals(userId)) {
            cart.setQuantity(quantity);
            cart = cartRepository.save(cart);
        }
        return toCartResponseDTO(cart);
    }

    public void deleteCartItem(Long cartId) {
        Long userId = userUtil.getAuthenticatedUser();
        Cart cart = cartRepository.findByCartIdAndUserId(cartId, userId);
        cartRepository.delete(cart);

    }

    public void deleteAllCartItems() {
        Long userId = userUtil.getAuthenticatedUser();
        List<Cart> userCarts = cartRepository.findByUserId(userId);
        cartRepository.deleteAll(userCarts);


    }

    private CartResponseDTO toCartResponseDTO(Cart cart) {
        if (cart == null) {
            return null;
        }
        CartResponseDTO cartResponseDTO = new CartResponseDTO();
        cartResponseDTO.setCartId(cart.getCartId());
        cartResponseDTO.setOptionId(cart.getOptionId());
        cartResponseDTO.setQuantity(cart.getQuantity());
        return cartResponseDTO;
    }
}