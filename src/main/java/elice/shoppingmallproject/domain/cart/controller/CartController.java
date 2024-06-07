package elice.shoppingmallproject.domain.cart.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import elice.shoppingmallproject.domain.cart.dto.CartCreateDTO;
import elice.shoppingmallproject.domain.cart.dto.CartResponseDTO;
import elice.shoppingmallproject.domain.cart.service.CartService;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping
    public CartResponseDTO addToCart(@RequestBody CartCreateDTO cartCreateDTO) {
        return cartService.addToCart(cartCreateDTO);
    }

    @GetMapping
    public List<CartResponseDTO> getCartItems(@RequestParam Long userId) {
        return cartService.getCartItems(userId);
    }

    @PutMapping("/{cartId}")
    public CartResponseDTO updateCartItem(@PathVariable Long cartId, @RequestBody int quantity) {
        return cartService.updateCartItem(cartId, quantity);
    }

    @DeleteMapping("/{cartId}")
    public void deleteCartItem(@PathVariable Long cartId) {
        cartService.deleteCartItem(cartId);
    }
}
