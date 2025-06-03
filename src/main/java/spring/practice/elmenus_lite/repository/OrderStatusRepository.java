package spring.practice.elmenus_lite.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spring.practice.elmenus_lite.model.OrderStatusModel;

import java.util.Optional;

@Repository
public interface OrderStatusRepository extends JpaRepository<OrderStatusModel, Integer> {
	Optional<OrderStatusModel> findByName(String name);
}
