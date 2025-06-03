package spring.practice.elmenus_lite.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spring.practice.elmenus_lite.model.OrderModel;
import spring.practice.elmenus_lite.model.OrderStatusModel;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<OrderModel, Integer> {
	Optional<OrderModel> findByOrderNumber(String orderNumber);

	List<OrderModel> findByCustomerId(Integer customerId);

	Page<OrderModel> findByCustomerId(Integer customerId, Pageable pageable);

	List<OrderModel> findByRestaurantId(Integer restaurantId);

	Page<OrderModel> findByRestaurantId(Integer restaurantId, Pageable pageable);

	List<OrderModel> findByStatus(OrderStatusModel status);

	List<OrderModel> findByStatusId(Integer statusId);

	List<OrderModel> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}
