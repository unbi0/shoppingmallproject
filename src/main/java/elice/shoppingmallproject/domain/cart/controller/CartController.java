package elice.shoppingmallproject.domain.cart.controller;

import java.util.List;

import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import elice.shoppingmallproject.domain.cart.dto.CartCreateDTO;
import elice.shoppingmallproject.domain.cart.dto.CartResponseDTO;
import elice.shoppingmallproject.domain.cart.service.CartService;
import elice.shoppingmallproject.global.util.UserUtil;

@RestController
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;
    private final UserUtil userUtil;

    public CartController(CartService cartService, UserUtil userUtil) {
        this.cartService = cartService;
        this.userUtil = userUtil;
    }

    @PostMapping
    public CartResponseDTO addToCart(@RequestBody CartCreateDTO cartCreateDTO) {
        // Null 체크 추가
        Assert.notNull(cartCreateDTO, "CartCreateDTO must not be null");
        Assert.notNull(cartCreateDTO.getOptionId(), "Option ID must not be null");
        return cartService.addCart(cartCreateDTO);
    }

    @GetMapping
    public List<CartResponseDTO> getCartItems() {
        return cartService.getCartItems();
    }

    @PutMapping("/{cartId}")
    public CartResponseDTO updateCartItem(@PathVariable Long cartId, @RequestBody int quantity) {
        return cartService.updateCartItem(cartId, quantity);
    }

    @GetMapping("/total")
    public double getTotalPrice() {
        return cartService.getTotalPrice();
    }

    @DeleteMapping("/{cartId}")
    public void deleteCartItem(@PathVariable Long cartId) {
        cartService.deleteCartItem(cartId);
    }

    @DeleteMapping("/all")
    public void deleteAllCartItems() {
        cartService.deleteAllCartItems();
    }
}
