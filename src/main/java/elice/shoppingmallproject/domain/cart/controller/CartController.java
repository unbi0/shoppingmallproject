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

    // 로컬 스토리지의 데이터를 서버로 전송하여 DB를 업데이트하는 메서드
    @PostMapping("/upload")
    public ResponseEntity<?> uploadCart(@RequestBody List<CartCreateDTO> localCartItems) {
        Long userId = userUtil.getAuthenticatedUser();
        if (userId == null) {
            throw new IllegalArgumentException("User ID must not be null");
        }

        for (CartCreateDTO item : localCartItems) {
            // 기존 항목이 있는지 확인
            Cart existingCart = cartService.findByUserIdAndOptionId(userId, item.getOptionId());
            if (existingCart != null) {
                // 기존 항목이 있다면 수량 업데이트
                existingCart.setQuantity(Math.max(existingCart.getQuantity(), item.getQuantity()));
                cartService.save(existingCart);
            } else {
                // 새로운 항목이라면 추가
                cartService.addCart(item);
            }
        }

        return ResponseEntity.ok("Cart uploaded successfully");
    }
}