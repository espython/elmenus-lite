package spring.practice.elmenus_lite.controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring.practice.elmenus_lite.exception.EntityNotFoundException;
import spring.practice.elmenus_lite.model.CartItemModel;
import spring.practice.elmenus_lite.service.CartService;
import spring.practice.elmenus_lite.statusCode.SuccessStatusCode;
import spring.practice.elmenus_lite.util.ApiResponse;

import java.util.Collections;
import java.util.List;

@RestController("/cart/")
@AllArgsConstructor
public class CartController {
    @Autowired
    private final CartService cartService;

    @PutMapping("{id}/items")
    public ResponseEntity<ApiResponse<?>> updateCartItems(@PathVariable("id") Integer cartId, @RequestBody List<CartItemModel> cartItems) {
        List<CartItemModel> updatedCartItems = cartService.updateCartItems(cartId, cartItems);
        ApiResponse<List<CartItemModel>> response = new ApiResponse<>(HttpStatus.OK.value(), SuccessStatusCode.CART_ITEMS_UPDATED_SUCCESSFULLY.getMessage(), updatedCartItems);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("{id}/items")
    public ResponseEntity<ApiResponse<?>> addCartItems(@PathVariable("id") Integer cartId, @RequestBody List<CartItemModel> cartItems) {
        try {
            List<CartItemModel> addedCartItems = cartService.addCartItems(cartId, cartItems);
            ApiResponse<List<CartItemModel>> response = new ApiResponse<>(HttpStatus.CREATED.value(),
                    SuccessStatusCode.CART_ITEMS_ADDED_SUCCESSFULLY.getMessage(),
                    addedCartItems);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (EntityNotFoundException e) {
            return createErrorResponse(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to add cart items: " + e.getMessage());
        }
    }

    @DeleteMapping("{id}/items/{itemId}")
    public ResponseEntity<ApiResponse<?>> deleteCartItem(@PathVariable("id") Integer cartId, @PathVariable("itemId") Integer itemId) {
        cartService.deleteCartItem(itemId);
        ApiResponse<Void> response = new ApiResponse<>(HttpStatus.OK.value(), SuccessStatusCode.CART_ITEMS_UPDATED_SUCCESSFULLY.getMessage(), null);
        return ResponseEntity.ok().body(response);
    }

    private ResponseEntity<ApiResponse<?>> createErrorResponse(HttpStatus status, String message) {
        return ResponseEntity
            .status(status)
                .body(new ApiResponse<>(status.value(), message, Collections.emptyList()));
    }
}