package spring.practice.elmenus_lite.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spring.practice.elmenus_lite.model.AddressModel;

@Repository
public interface AddressRepository extends JpaRepository<AddressModel, Integer> {
}
