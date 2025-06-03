package spring.practice.elmenus_lite.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.practice.elmenus_lite.exception.EntityNotFoundException;
import spring.practice.elmenus_lite.model.*;
import spring.practice.elmenus_lite.repository.*;
import spring.practice.elmenus_lite.statusCode.ErrorMessage;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OrderService {
	private final OrderRepository orderRepository;
	private final OrderItemRepository orderItemRepository;
	private final OrderStatusRepository orderStatusRepository;
	private final CustomerRepository customerRepository;
	private final RestaurantRepository restaurantRepository;
	private final AddressRepository addressRepository;
	private final MenuItemRepository menuItemRepository;
	private final CartRepository cartRepository;
	private final CartItemRepository cartItemRepository;

	/**
	 * Find an order by its ID
	 * 
	 * @param orderId the ID of the order to find
	 * @return optional containing the order if found
	 */
	public Optional<OrderModel> findById(Integer orderId) {
		return orderRepository.findById(orderId);
	}

	/**
	 * Find an order by its order number
	 * 
	 * @param orderNumber the order number to search for
	 * @return optional containing the order if found
	 */
	public Optional<OrderModel> findByOrderNumber(String orderNumber) {
		return orderRepository.findByOrderNumber(orderNumber);
	}

	/**
	 * Get all orders for a specific customer
	 * 
	 * @param customerId the ID of the customer
	 * @return list of orders for the customer
	 */
	public List<OrderModel> getOrdersByCustomerId(Integer customerId) {
		// Verify customer exists
		if (!customerRepository.existsById(customerId)) {
			throw new EntityNotFoundException("Customer not found with ID: " + customerId);
		}
		return orderRepository.findByCustomerId(customerId);
	}

	/**
	 * Get all orders for a specific restaurant
	 * 
	 * @param restaurantId the ID of the restaurant
	 * @return list of orders for the restaurant
	 */
	public List<OrderModel> getOrdersByRestaurantId(Integer restaurantId) {
		// Verify restaurant exists
		if (!restaurantRepository.existsById(restaurantId)) {
			throw new EntityNotFoundException("Restaurant not found with ID: " + restaurantId);
		}
		return orderRepository.findByRestaurantId(restaurantId);
	}

	/**
	 * Get all orders with a specific status
	 * 
	 * @param statusId the ID of the status
	 * @return list of orders with the given status
	 */
	public List<OrderModel> getOrdersByStatusId(Integer statusId) {
		// Verify status exists
		if (!orderStatusRepository.existsById(statusId)) {
			throw new EntityNotFoundException("Order status not found with ID: " + statusId);
		}
		return orderRepository.findByStatusId(statusId);
	}

	/**
	 * Create a new order from the customer's cart
	 * 
	 * @param customerId          the ID of the customer
	 * @param restaurantId        the ID of the restaurant
	 * @param addressId           the ID of the delivery address
	 * @param specialInstructions any special instructions for the order
	 * @return the created order
	 */
	@Transactional
	public OrderModel createOrderFromCart(Integer customerId, Integer restaurantId, Integer addressId,
			String specialInstructions) {
		// Find and validate customer's cart
		CartModel cart = cartRepository.findByCustomerId(customerId)
				.orElseThrow(() -> new EntityNotFoundException("Cart not found for customer ID: " + customerId));

		// Check if cart has items
		List<CartItemModel> cartItems = cartItemRepository.findAllByCartId(cart.getId());
		if (cartItems.isEmpty()) {
			throw new IllegalStateException("Cannot create order from an empty cart");
		}

		// Find and validate customer, restaurant and address
		CustomerModel customer = customerRepository.findById(customerId)
				.orElseThrow(() -> new EntityNotFoundException("Customer not found with ID: " + customerId));

		RestaurantModel restaurant = restaurantRepository.findById(restaurantId)
				.orElseThrow(() -> new EntityNotFoundException("Restaurant not found with ID: " + restaurantId));

		AddressModel address = addressRepository.findById(addressId)
				.orElseThrow(() -> new EntityNotFoundException("Address not found with ID: " + addressId));

		// Get initial order status (e.g., "PENDING")
		OrderStatusModel initialStatus = orderStatusRepository.findByName("PENDING")
				.orElseThrow(() -> new EntityNotFoundException("Initial order status 'PENDING' not found"));

		// Create new order
		OrderModel order = new OrderModel();
		order.setCustomer(customer);
		order.setRestaurant(restaurant);
		order.setDeliveryAddress(address);
		order.setStatus(initialStatus);
		order.setOrderNumber(generateOrderNumber());
		order.setSpecialInstructions(specialInstructions);

		// Calculate prices
		BigDecimal subtotal = BigDecimal.ZERO;
		for (CartItemModel cartItem : cartItems) {
			subtotal = subtotal.add(
					BigDecimal.valueOf(cartItem.getMenuItem().getPrice()).multiply(BigDecimal.valueOf(cartItem.getQuantity())));
		}

		// Set financial details (these could be made configurable)
		BigDecimal deliveryFee = new BigDecimal("5.00");
		BigDecimal taxRate = new BigDecimal("0.1"); // 10% tax
		BigDecimal tax = subtotal.multiply(taxRate);
		BigDecimal total = subtotal.add(deliveryFee).add(tax);

		order.setSubtotal(subtotal);
		order.setDeliveryFee(deliveryFee);
		order.setTax(tax);
		order.setTotal(total);

		// Set estimated delivery time (e.g., 45 minutes from now)
		order.setEstimatedDeliveryTime(LocalDateTime.now().plusMinutes(45));

		// Save the order first to get its ID
		OrderModel savedOrder = orderRepository.save(order);

		// Create order items from cart items
		List<OrderItemModel> orderItems = new ArrayList<>();
		for (CartItemModel cartItem : cartItems) {
			OrderItemModel orderItem = new OrderItemModel();
			orderItem.setOrder(savedOrder);
			orderItem.setMenuItem(cartItem.getMenuItem());
			orderItem.setQuantity(cartItem.getQuantity());
			orderItem.setUnitPrice(BigDecimal.valueOf(cartItem.getMenuItem().getPrice()));
			orderItem.setSubtotal(
					BigDecimal.valueOf(cartItem.getMenuItem().getPrice()).multiply(BigDecimal.valueOf(cartItem.getQuantity())));
			orderItems.add(orderItem);
		}

		// Save all order items
		orderItemRepository.saveAll(orderItems);

		// Clear the cart after order is created
		cartItemRepository.deleteAll(cartItems);

		return savedOrder;
	}

	/**
	 * Create a new order directly (without using a cart)
	 * 
	 * @param customerId          the ID of the customer
	 * @param restaurantId        the ID of the restaurant
	 * @param addressId           the ID of the delivery address
	 * @param orderItems          the list of order items to add
	 * @param specialInstructions any special instructions for the order
	 * @return the created order
	 */
	@Transactional
	public OrderModel createOrder(Integer customerId, Integer restaurantId, Integer addressId,
			List<OrderItemModel> orderItems, String specialInstructions) {
		if (orderItems == null || orderItems.isEmpty()) {
			throw new IllegalArgumentException("Order must contain at least one item");
		}

		// Find and validate customer, restaurant and address
		CustomerModel customer = customerRepository.findById(customerId)
				.orElseThrow(() -> new EntityNotFoundException("Customer not found with ID: " + customerId));

		RestaurantModel restaurant = restaurantRepository.findById(restaurantId)
				.orElseThrow(() -> new EntityNotFoundException("Restaurant not found with ID: " + restaurantId));

		AddressModel address = addressRepository.findById(addressId)
				.orElseThrow(() -> new EntityNotFoundException("Address not found with ID: " + addressId));

		// Get initial order status (e.g., "PENDING")
		OrderStatusModel initialStatus = orderStatusRepository.findByName("PENDING")
				.orElseThrow(() -> new EntityNotFoundException("Initial order status 'PENDING' not found"));

		// Create new order
		OrderModel order = new OrderModel();
		order.setCustomer(customer);
		order.setRestaurant(restaurant);
		order.setDeliveryAddress(address);
		order.setStatus(initialStatus);
		order.setOrderNumber(generateOrderNumber());
		order.setSpecialInstructions(specialInstructions);

		// Validate menu items and calculate prices
		BigDecimal subtotal = BigDecimal.ZERO;
		for (OrderItemModel item : orderItems) {
			Integer menuItemId = item.getMenuItem().getId();
			MenuItemModel menuItem = menuItemRepository.findById(menuItemId)
					.orElseThrow(() -> new EntityNotFoundException("Menu item not found with ID: " + menuItemId));

			item.setMenuItem(menuItem);
			item.setUnitPrice(BigDecimal.valueOf(menuItem.getPrice()));
			item.setSubtotal(BigDecimal.valueOf(menuItem.getPrice()).multiply(BigDecimal.valueOf(item.getQuantity())));

			subtotal = subtotal.add(item.getSubtotal());
		}

		// Set financial details (these could be made configurable)
		BigDecimal deliveryFee = new BigDecimal("5.00");
		BigDecimal taxRate = new BigDecimal("0.1"); // 10% tax
		BigDecimal tax = subtotal.multiply(taxRate);
		BigDecimal total = subtotal.add(deliveryFee).add(tax);

		order.setSubtotal(subtotal);
		order.setDeliveryFee(deliveryFee);
		order.setTax(tax);
		order.setTotal(total);

		// Set estimated delivery time (e.g., 45 minutes from now)
		order.setEstimatedDeliveryTime(LocalDateTime.now().plusMinutes(45));

		// Save the order first to get its ID
		OrderModel savedOrder = orderRepository.save(order);

		// Set order reference for each item and save them
		orderItems.forEach(item -> item.setOrder(savedOrder));
		orderItemRepository.saveAll(orderItems);

		return savedOrder;
	}

	/**
	 * Update the status of an order
	 * 
	 * @param orderId  the ID of the order to update
	 * @param statusId the ID of the new status
	 * @return the updated order
	 */
	@Transactional
	public OrderModel updateOrderStatus(Integer orderId, Integer statusId) {
		// Find and validate order
		OrderModel order = orderRepository.findById(orderId)
				.orElseThrow(() -> new EntityNotFoundException("Order not found with ID: " + orderId));

		// Find and validate status
		OrderStatusModel status = orderStatusRepository.findById(statusId)
				.orElseThrow(() -> new EntityNotFoundException("Order status not found with ID: " + statusId));

		// Update status
		order.setStatus(status);

		// Update estimated delivery time based on status if needed
		if (status.getName().equals("IN_TRANSIT")) {
			order.setEstimatedDeliveryTime(LocalDateTime.now().plusMinutes(20));
		}

		return orderRepository.save(order);
	}

	/**
	 * Cancel an order - sets status to CANCELLED
	 * 
	 * @param orderId the ID of the order to cancel
	 * @return the updated order
	 */
	@Transactional
	public OrderModel cancelOrder(Integer orderId) {
		// Find and validate order
		OrderModel order = orderRepository.findById(orderId)
				.orElseThrow(() -> new EntityNotFoundException("Order not found with ID: " + orderId));

		// Find cancelled status
		OrderStatusModel cancelledStatus = orderStatusRepository.findByName("CANCELLED")
				.orElseThrow(() -> new EntityNotFoundException("Order status 'CANCELLED' not found"));

		// Cannot cancel already delivered orders
		if (order.getStatus().getName().equals("DELIVERED")) {
			throw new IllegalStateException("Cannot cancel an order that has already been delivered");
		}

		// Update status
		order.setStatus(cancelledStatus);

		return orderRepository.save(order);
	}

	/**
	 * Get all items for a specific order
	 * 
	 * @param orderId the ID of the order
	 * @return list of order items
	 */
	public List<OrderItemModel> getOrderItems(Integer orderId) {
		// Verify order exists
		if (!orderRepository.existsById(orderId)) {
			throw new EntityNotFoundException("Order not found with ID: " + orderId);
		}
		return orderItemRepository.findByOrderId(orderId);
	}

	/**
	 * Generate a unique order number
	 * 
	 * @return a unique order number
	 */
	private String generateOrderNumber() {
		String timestamp = String.valueOf(System.currentTimeMillis());
		String random = String.valueOf(new Random().nextInt(10000));
		return "ORD-" + timestamp.substring(timestamp.length() - 6) + "-" + random;
	}
}
