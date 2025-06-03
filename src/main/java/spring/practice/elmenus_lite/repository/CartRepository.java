package spring.practice.elmenus_lite.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spring.practice.elmenus_lite.model.CartModel;

import java.util.Optional;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<CartModel, Integer> {
	Optional<CartModel> findByCustomerId(Integer customerId);
}