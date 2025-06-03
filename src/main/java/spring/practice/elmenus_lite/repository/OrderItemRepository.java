package spring.practice.elmenus_lite.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import spring.practice.elmenus_lite.model.OrderItemModel;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItemModel, Integer> {
	List<OrderItemModel> findByOrderId(Integer orderId);

	List<OrderItemModel> findByMenuItemId(Integer menuItemId);

	@Query("SELECT oi FROM OrderItemModel oi WHERE oi.order.customer.id = :customerId")
	List<OrderItemModel> findByCustomerId(Integer customerId);

	@Query("SELECT oi FROM OrderItemModel oi WHERE oi.order.restaurant.id = :restaurantId")
	List<OrderItemModel> findByRestaurantId(Integer restaurantId);

	@Query("SELECT SUM(oi.quantity) FROM OrderItemModel oi WHERE oi.menuItem.id = :menuItemId")
	Long countTotalQuantityByMenuItemId(Integer menuItemId);
}
