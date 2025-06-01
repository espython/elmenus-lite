package spring.practice.elmenus_lite.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.practice.elmenus_lite.exception.EntityNotFoundException;
import spring.practice.elmenus_lite.model.CartItemModel;
import spring.practice.elmenus_lite.model.CartModel;
import spring.practice.elmenus_lite.repository.CartItemRepository;
import spring.practice.elmenus_lite.repository.CartRepository;
import spring.practice.elmenus_lite.statusCode.ErrorMessage;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    
    // Cart methods
    public Optional<CartModel> findById(Integer cartId) {
        return cartRepository.findById(cartId);
    }
    
    // Cart item methods
    public List<CartItemModel> addCartItems(Integer cartId, List<CartItemModel> cartItems) {
        // Find cart first to validate it exists
        CartModel cart = findById(cartId).orElseThrow(() -> 
            new EntityNotFoundException(ErrorMessage.CART_NOT_FOUND.getMessage()));
            
        // Set cart reference for each item
        cartItems.forEach(item -> item.setCart(cart));
        
        // Save all items
        return cartItemRepository.saveAll(cartItems);
    }
    
    public void deleteCartItem(Integer id) {
        cartItemRepository.deleteById(id);
    }
    
    @Transactional
    public List<CartItemModel> updateCartItems(Integer cartId, List<CartItemModel> cartItems) {
        if (cartItems == null || cartItems.isEmpty()) {
            return Collections.emptyList();
        }
        
        // Find cart first to validate it exists
        CartModel cart = findById(cartId).orElseThrow(() -> 
            new EntityNotFoundException(ErrorMessage.CART_NOT_FOUND.getMessage()));
        
        // Set cart reference for each item and ensure each item has an ID
        cartItems.forEach(item -> {
            if (item.getId() == null) {
                throw new IllegalArgumentException("Cart item ID cannot be null for update operation");
            }
            item.setCart(cart);
        });
        
        // Check if all items exist in the database and belong to this cart
        List<Integer> itemIds = cartItems.stream().map(CartItemModel::getId).distinct().toList();
        List<CartItemModel> existingItems = cartItemRepository.findAllByIdInAndCartId(itemIds, cartId);
        
        if (existingItems.size() != itemIds.size()) {
            Set<Integer> existingIds = existingItems.stream().map(CartItemModel::getId).collect(Collectors.toSet());
            List<Integer> missingIds = itemIds.stream().filter(id -> !existingIds.contains(id)).toList();
            throw new EntityNotFoundException("Cart items not found or don't belong to this cart: " + missingIds);
        }
        
        // Save all updated items
        return cartItemRepository.saveAll(cartItems);
    }
}