package spring.practice.elmenus_lite.controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring.practice.elmenus_lite.exception.EntityNotFoundException;
import spring.practice.elmenus_lite.model.CartItemModel;
import spring.practice.elmenus_lite.model.CartModel;
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
        try {
            List<CartItemModel> updatedCartItems = cartService.updateCartItems(cartId, cartItems);
            ApiResponse<List<CartItemModel>> response = new ApiResponse<>(HttpStatus.OK.value(), SuccessStatusCode.CART_ITEMS_UPDATED_SUCCESSFULLY.getMessage(), updatedCartItems);
            return ResponseEntity.ok().body(response);
        } catch (EntityNotFoundException e) {
            return ApiResponse.error(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(HttpStatus.BAD_REQUEST, e.getMessage());
        } 
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
            return ApiResponse.error(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to add cart items: " + e.getMessage());
        }
    }

    @DeleteMapping("{id}/items/{itemId}")
    public ResponseEntity<ApiResponse<?>> deleteCartItem(@PathVariable("id") Integer cartId, @PathVariable("itemId") Integer itemId) {
        try {
            cartService.deleteCartItem(itemId);
            ApiResponse<Void> response = new ApiResponse<>(HttpStatus.OK.value(), SuccessStatusCode.CART_ITEMS_UPDATED_SUCCESSFULLY.getMessage(), null);
            return ResponseEntity.ok().body(response);
        } catch (EntityNotFoundException e) {
            return ApiResponse.error(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("{id}")
    public ResponseEntity<ApiResponse<?>> getCart(@PathVariable("id") Integer customerId) {
        try {
            CartModel cart = cartService.getCart(customerId);
            ApiResponse<CartModel> response = new ApiResponse<>(HttpStatus.OK.value(), SuccessStatusCode.CART_ITEMS_UPDATED_SUCCESSFULLY.getMessage(), cart);
            return ResponseEntity.ok().body(response);
        } catch (EntityNotFoundException e) {
            return ApiResponse.error(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("customers")
    public ResponseEntity<ApiResponse<?>> getCartByCustomerId(@RequestParam("customerId") Integer customerId) {
        try {
            CartModel cart = cartService.getCart(customerId);
            ApiResponse<CartModel> response = new ApiResponse<>(HttpStatus.OK.value(), SuccessStatusCode.CART_ITEMS_UPDATED_SUCCESSFULLY.getMessage(), cart);
            return ResponseEntity.ok().body(response);
        } catch (EntityNotFoundException e) {
            return ApiResponse.error(HttpStatus.NOT_FOUND, e.getMessage());
        } 
    }
    
}