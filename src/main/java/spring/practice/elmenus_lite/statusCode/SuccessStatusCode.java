package spring.practice.elmenus_lite.statusCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SuccessStatusCode {
    CART_ITEMS_UPDATED_SUCCESSFULLY("Cart Items Updated Successfully"),
    CART_ITEMS_ADDED_SUCCESSFULLY("Cart Items Added Successfully");

    private final String message;
}
