package spring.practice.elmenus_lite.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spring.practice.elmenus_lite.model.RestaurantModel;

import java.util.List;
import java.util.Optional;

@Repository
public interface RestaurantRepository extends JpaRepository<RestaurantModel, Integer> {
	Optional<RestaurantModel> findByRestaurantName(String restaurantName);

	List<RestaurantModel> findByActive(boolean active);
}
