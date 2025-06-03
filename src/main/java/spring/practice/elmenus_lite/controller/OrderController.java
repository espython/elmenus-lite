package spring.practice.elmenus_lite.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring.practice.elmenus_lite.exception.EntityNotFoundException;
import spring.practice.elmenus_lite.model.OrderItemModel;
import spring.practice.elmenus_lite.model.OrderModel;
import spring.practice.elmenus_lite.service.OrderService;
import spring.practice.elmenus_lite.statusCode.SuccessStatusCode;
import spring.practice.elmenus_lite.util.ApiResponse;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/orders")
@AllArgsConstructor
public class OrderController {

	private final OrderService orderService;

	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<?>> getOrderById(@PathVariable("id") Integer orderId) {
		try {
			OrderModel order = orderService.findById(orderId)
					.orElseThrow(() -> new EntityNotFoundException("Order not found with ID: " + orderId));

			ApiResponse<OrderModel> response = new ApiResponse<>(
					HttpStatus.OK.value(),
					"Order retrieved successfully",
					order);
			return ResponseEntity.ok().body(response);
		} catch (EntityNotFoundException e) {
			return ApiResponse.error(HttpStatus.NOT_FOUND, e.getMessage());
		} catch (Exception e) {
			return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve order: " + e.getMessage());
		}
	}

	@GetMapping("/number/{orderNumber}")
	public ResponseEntity<ApiResponse<?>> getOrderByNumber(@PathVariable("orderNumber") String orderNumber) {
		try {
			OrderModel order = orderService.findByOrderNumber(orderNumber)
					.orElseThrow(() -> new EntityNotFoundException("Order not found with number: " + orderNumber));

			ApiResponse<OrderModel> response = new ApiResponse<>(
					HttpStatus.OK.value(),
					"Order retrieved successfully",
					order);
			return ResponseEntity.ok().body(response);
		} catch (EntityNotFoundException e) {
			return ApiResponse.error(HttpStatus.NOT_FOUND, e.getMessage());
		} catch (Exception e) {
			return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve order: " + e.getMessage());
		}
	}

	@GetMapping("/customer/{customerId}")
	public ResponseEntity<ApiResponse<?>> getOrdersByCustomerId(@PathVariable("customerId") Integer customerId) {
		try {
			List<OrderModel> orders = orderService.getOrdersByCustomerId(customerId);

			ApiResponse<List<OrderModel>> response = new ApiResponse<>(
					HttpStatus.OK.value(),
					"Orders retrieved successfully",
					orders);
			return ResponseEntity.ok().body(response);
		} catch (EntityNotFoundException e) {
			return ApiResponse.error(HttpStatus.NOT_FOUND, e.getMessage());
		} catch (Exception e) {
			return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve orders: " + e.getMessage());
		}
	}

	@GetMapping("/restaurant/{restaurantId}")
	public ResponseEntity<ApiResponse<?>> getOrdersByRestaurantId(@PathVariable("restaurantId") Integer restaurantId) {
		try {
			List<OrderModel> orders = orderService.getOrdersByRestaurantId(restaurantId);

			ApiResponse<List<OrderModel>> response = new ApiResponse<>(
					HttpStatus.OK.value(),
					"Orders retrieved successfully",
					orders);
			return ResponseEntity.ok().body(response);
		} catch (EntityNotFoundException e) {
			return ApiResponse.error(HttpStatus.NOT_FOUND, e.getMessage());
		} catch (Exception e) {
			return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve orders: " + e.getMessage());
		}
	}

	@GetMapping("/status/{statusId}")
	public ResponseEntity<ApiResponse<?>> getOrdersByStatusId(@PathVariable("statusId") Integer statusId) {
		try {
			List<OrderModel> orders = orderService.getOrdersByStatusId(statusId);

			ApiResponse<List<OrderModel>> response = new ApiResponse<>(
					HttpStatus.OK.value(),
					"Orders retrieved successfully",
					orders);
			return ResponseEntity.ok().body(response);
		} catch (EntityNotFoundException e) {
			return ApiResponse.error(HttpStatus.NOT_FOUND, e.getMessage());
		} catch (Exception e) {
			return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve orders: " + e.getMessage());
		}
	}

	@GetMapping("/{id}/items")
	public ResponseEntity<ApiResponse<?>> getOrderItems(@PathVariable("id") Integer orderId) {
		try {
			List<OrderItemModel> orderItems = orderService.getOrderItems(orderId);

			ApiResponse<List<OrderItemModel>> response = new ApiResponse<>(
					HttpStatus.OK.value(),
					"Order items retrieved successfully",
					orderItems);
			return ResponseEntity.ok().body(response);
		} catch (EntityNotFoundException e) {
			return ApiResponse.error(HttpStatus.NOT_FOUND, e.getMessage());
		} catch (Exception e) {
			return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve order items: " + e.getMessage());
		}
	}

	@PostMapping("/from-cart")
	public ResponseEntity<ApiResponse<?>> createOrderFromCart(@RequestBody Map<String, Object> orderRequest) {
		try {
			Integer customerId = (Integer) orderRequest.get("customerId");
			Integer restaurantId = (Integer) orderRequest.get("restaurantId");
			Integer addressId = (Integer) orderRequest.get("addressId");
			String specialInstructions = (String) orderRequest.get("specialInstructions");

			OrderModel createdOrder = orderService.createOrderFromCart(
					customerId, restaurantId, addressId, specialInstructions);

			ApiResponse<OrderModel> response = new ApiResponse<>(
					HttpStatus.CREATED.value(),
					"Order created successfully from cart",
					createdOrder);
			return ResponseEntity.status(HttpStatus.CREATED).body(response);
		} catch (EntityNotFoundException e) {
			return ApiResponse.error(HttpStatus.NOT_FOUND, e.getMessage());
		} catch (IllegalStateException | IllegalArgumentException e) {
			return ApiResponse.error(HttpStatus.BAD_REQUEST, e.getMessage());
		} catch (Exception e) {
			return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create order: " + e.getMessage());
		}
	}

	@PostMapping
	public ResponseEntity<ApiResponse<?>> createOrder(@RequestBody Map<String, Object> orderRequest) {
		try {
			Integer customerId = (Integer) orderRequest.get("customerId");
			Integer restaurantId = (Integer) orderRequest.get("restaurantId");
			Integer addressId = (Integer) orderRequest.get("addressId");
			String specialInstructions = (String) orderRequest.get("specialInstructions");
			List<OrderItemModel> orderItems = (List<OrderItemModel>) orderRequest.get("orderItems");

			OrderModel createdOrder = orderService.createOrder(
					customerId, restaurantId, addressId, orderItems, specialInstructions);

			ApiResponse<OrderModel> response = new ApiResponse<>(
					HttpStatus.CREATED.value(),
					"Order created successfully",
					createdOrder);
			return ResponseEntity.status(HttpStatus.CREATED).body(response);
		} catch (EntityNotFoundException e) {
			return ApiResponse.error(HttpStatus.NOT_FOUND, e.getMessage());
		} catch (IllegalStateException | IllegalArgumentException e) {
			return ApiResponse.error(HttpStatus.BAD_REQUEST, e.getMessage());
		} catch (Exception e) {
			return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create order: " + e.getMessage());
		}
	}

	@PatchMapping("/{id}/status/{statusId}")
	public ResponseEntity<ApiResponse<?>> updateOrderStatus(
			@PathVariable("id") Integer orderId,
			@PathVariable("statusId") Integer statusId) {
		try {
			OrderModel updatedOrder = orderService.updateOrderStatus(orderId, statusId);

			ApiResponse<OrderModel> response = new ApiResponse<>(
					HttpStatus.OK.value(),
					"Order status updated successfully",
					updatedOrder);
			return ResponseEntity.ok().body(response);
		} catch (EntityNotFoundException e) {
			return ApiResponse.error(HttpStatus.NOT_FOUND, e.getMessage());
		} catch (Exception e) {
			return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update order status: " + e.getMessage());
		}
	}

	@PatchMapping("/{id}/cancel")
	public ResponseEntity<ApiResponse<?>> cancelOrder(@PathVariable("id") Integer orderId) {
		try {
			OrderModel cancelledOrder = orderService.cancelOrder(orderId);

			ApiResponse<OrderModel> response = new ApiResponse<>(
					HttpStatus.OK.value(),
					"Order cancelled successfully",
					cancelledOrder);
			return ResponseEntity.ok().body(response);
		} catch (EntityNotFoundException e) {
			return ApiResponse.error(HttpStatus.NOT_FOUND, e.getMessage());
		} catch (IllegalStateException e) {
			return ApiResponse.error(HttpStatus.BAD_REQUEST, e.getMessage());
		} catch (Exception e) {
			return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to cancel order: " + e.getMessage());
		}
	}
}
