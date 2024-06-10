package elice.shoppingmallproject.domain.cart.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import elice.shoppingmallproject.domain.cart.dto.CartCreateDTO;
import elice.shoppingmallproject.domain.cart.dto.CartResponseDTO;
import elice.shoppingmallproject.domain.cart.entity.Cart;
import elice.shoppingmallproject.domain.cart.repository.CartRepository;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    public CartResponseDTO addCart(CartCreateDTO cartCreateDTO) {
        Cart cart = new Cart();
        cart.setOptionId(cartCreateDTO.getOptionId());
        cart.setUserId(cartCreateDTO.getUserId());
        cart.setQuantity(cartCreateDTO.getQuantity());
        cart = cartRepository.save(cart);
        return toCartResponseDTO(cart);
    }

    public List<CartResponseDTO> getCartItems(Long userId) {
        List<Cart> cartItems = cartRepository.findByUserId(userId);
        return cartItems.stream().map(this::toCartResponseDTO).collect(Collectors.toList());
    }

    public CartResponseDTO updateCartItem(Long cartId, int quantity) {
        Cart cart = cartRepository.findById(cartId).orElse(null);
        if (cart != null) {
            cart.setQuantity(quantity);
            cart = cartRepository.save(cart);
        }
        return toCartResponseDTO(cart);
    }

    public void deleteCartItem(Long cartId) {
        cartRepository.deleteById(cartId);
    }

    private CartResponseDTO toCartResponseDTO(Cart cart) {
        CartResponseDTO cartResponseDTO = new CartResponseDTO();
        cartResponseDTO.setCartId(cart.getCartId());
        cartResponseDTO.setOptionId(cart.getOptionId());
        cartResponseDTO.setUserId(cart.getUserId());
        cartResponseDTO.setQuantity(cart.getQuantity());
        return cartResponseDTO;
    }
}
