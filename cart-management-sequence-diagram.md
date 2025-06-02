# Cart Management Sequence Diagram

This diagram illustrates the interactions between components in the cart management system of the Elmenus Lite application.

## Components
- **Client**: The frontend application or API client making requests
- **CartController**: REST controller that handles cart-related HTTP requests
- **CartService**: Business logic layer for cart operations
- **CartRepository/CartItemRepository**: Data access layer for cart entities
- **CartItemValidator**: Validates cart items before operations
- **CartApiDtoMapper**: Maps between API DTOs and internal DTOs
- **GlobalExceptionHandler**: Handles exceptions and returns appropriate HTTP responses

## 1. Get Cart by User ID

```mermaid
sequenceDiagram
    participant Client
    participant CartController
    participant CartService
    participant CartRepository
    participant CartApiDtoMapper
    participant GlobalExceptionHandler
    
    Client->>CartController: GET /api/cart/{customerId}
    CartController->>CartService: getCartByUserId(customerId)
    CartService->>CartRepository: findByCustomerId(customerId)
    
    alt Cart not found
        CartRepository-->>CartService: Empty Optional
        CartService->>GlobalExceptionHandler: throw EntityNotFoundException
        GlobalExceptionHandler-->>Client: 404 Not Found with error message
    else Cart found
        CartRepository-->>CartService: CartModel
        CartService-->>CartController: CartResponseDto
        CartController->>CartApiDtoMapper: mapCartResponseDtoToApiDto(cartDto)
        CartApiDtoMapper-->>CartController: CartResponseApiDto
        CartController-->>Client: 200 OK with CartResponseApiDto
    end
```

## 2. Add/Update Cart Items

```mermaid
sequenceDiagram
    participant Client
    participant CartController
    participant CartService
    participant CartRepository
    participant CartItemValidator
    participant CartApiDtoMapper
    participant CartItemRepository
    participant GlobalExceptionHandler
    
    Client->>CartController: POST /api/cart/{id} with CartItemRequestApiDto[]
    CartController->>CartApiDtoMapper: mapCartItemApiDtosToDtos(cartItemsApiDto)
    CartApiDtoMapper-->>CartController: List<CartItemDto>
    CartController->>CartService: addItemsToCart(cartId, cartItemDtos)
    CartService->>CartRepository: findById(cartId)
    
    alt Cart not found
        CartRepository-->>CartService: Empty Optional
        CartService->>GlobalExceptionHandler: throw EntityNotFoundException
        GlobalExceptionHandler-->>Client: 404 Not Found with error message
    else Cart found
        CartRepository-->>CartService: CartModel
        CartService->>CartItemValidator: validateMenuItems(cartItemDtos)
        
        alt Invalid menu items
            CartItemValidator->>GlobalExceptionHandler: throw BadRequestException
            GlobalExceptionHandler-->>Client: 400 Bad Request with error message
        else Valid menu items
            CartItemValidator-->>CartService: List<MenuItemModel>
            
            loop For each item
                CartService->>CartService: Update existing or add new items
            end
            
            CartService->>CartItemRepository: saveAll(updatedItems)
            CartItemRepository-->>CartService: Saved items
            CartService-->>CartController: void
            CartController-->>Client: 200 OK with success message
        end
    end
```

## 3. Get Cart Items

```mermaid
sequenceDiagram
    participant Client
    participant CartController
    participant CartService
    participant CartRepository
    participant CartApiDtoMapper
    participant GlobalExceptionHandler
    
    Client->>CartController: GET /api/cart/{id}/items
    CartController->>CartService: getAllItems(cartId)
    CartService->>CartRepository: findById(cartId)
    
    alt Cart not found
        CartRepository-->>CartService: Empty Optional
        CartService->>GlobalExceptionHandler: throw EntityNotFoundException
        GlobalExceptionHandler-->>Client: 404 Not Found with error message
    else Cart found
        CartRepository-->>CartService: CartModel
        CartService-->>CartController: CartResponseDto
        CartController->>CartApiDtoMapper: mapCartResponseDtoToApiDto(cartResponseDto)
        CartApiDtoMapper-->>CartController: CartResponseApiDto
        CartController-->>Client: 200 OK with CartResponseApiDto
    end
```

## 4. Clear Cart

```mermaid
sequenceDiagram
    participant Client
    participant CartController
    participant CartService
    participant CartRepository
    participant CartItemRepository
    participant GlobalExceptionHandler
    
    Client->>CartController: DELETE /api/cart/{id}/clear
    CartController->>CartService: clearCart(cartId)
    CartService->>CartRepository: findById(cartId)
    
    alt Cart not found
        CartRepository-->>CartService: Empty Optional
        CartService->>GlobalExceptionHandler: throw EntityNotFoundException
        GlobalExceptionHandler-->>Client: 404 Not Found with error message
    else Cart found
        CartRepository-->>CartService: CartModel
        
        alt Empty cart
            CartService->>GlobalExceptionHandler: throw BadRequestException
            GlobalExceptionHandler-->>Client: 400 Bad Request with error message
        else Cart has items
            CartService->>CartItemRepository: deleteAllByIdInBatch(itemIds)
            CartItemRepository-->>CartService: void
            CartService-->>CartController: void
            CartController-->>Client: 200 OK with success message
        end
    end
```

## Component Descriptions

### Main Components:
- **Client**: The frontend application or API client making requests
- **CartController**: REST controller that handles cart-related HTTP requests
- **CartService**: Business logic layer for cart operations
- **CartRepository/CartItemRepository**: Data access layer for cart entities
- **CartItemValidator**: Validates cart items before operations
- **CartApiDtoMapper**: Maps between API DTOs and internal DTOs
- **GlobalExceptionHandler**: Handles exceptions and returns appropriate HTTP responses

### Key Flows:

1. **Get Cart by User ID**:
   - Retrieves a user's cart with all items
   - Maps internal DTOs to API DTOs before returning to client

2. **Add/Update Cart Items**:
   - Validates menu items
   - Updates existing items or adds new ones
   - Handles batch operations efficiently

3. **Get Cart Items**:
   - Returns all items in a specific cart
   - Similar flow to getting cart by user ID

4. **Clear Cart**:
   - Removes all items from a cart
   - Validates cart existence and that it's not already empty
