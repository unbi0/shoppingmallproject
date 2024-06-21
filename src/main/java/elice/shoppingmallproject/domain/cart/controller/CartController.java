package elice.shoppingmallproject.domain.cart.controller;

import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import elice.shoppingmallproject.domain.cart.dto.CartCreateDTO;
import elice.shoppingmallproject.domain.cart.dto.CartResponseDTO;
import elice.shoppingmallproject.domain.cart.entity.Cart;
import elice.shoppingmallproject.domain.cart.service.CartService;
import elice.shoppingmallproject.global.util.UserUtil;
import org.springframework.http.ResponseEntity; // ResponseEntity import 추가

import java.util.List;
import java.util.Map;

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
        Assert.notNull(cartCreateDTO, "CartCreateDTO must not be null");
        Assert.notNull(cartCreateDTO.getOptionId(), "Option ID must not be null");
        return cartService.addCart(cartCreateDTO);
    }

    @GetMapping
    public List<CartResponseDTO> getCartItems() {
        return cartService.getCartItems();
    }

    @PutMapping("/{cartId}")
    public CartResponseDTO updateCartItem(@PathVariable("cartId") Long cartId, @RequestBody Map<String, Integer> request) {
        int quantity = request.get("quantity");
        System.out.println("Updating cart item: " + cartId + " with quantity: " + quantity);
        return cartService.updateCartItem(cartId, quantity);
    }

    @GetMapping("/total")
    public double getTotalPrice() {
        return cartService.getTotalPrice();
    }

    @DeleteMapping("/{cartId}")
    public void deleteCartItem(@PathVariable("cartId") Long cartId) {
        cartService.deleteCartItem(cartId);
    }

    @DeleteMapping("/all")
    public void deleteAllCartItems() {
        cartService.deleteAllCartItems();
    }
}