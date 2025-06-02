package spring.practice.elmenus_lite.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spring.practice.elmenus_lite.model.CartItemModel;

import java.util.List;


@Repository
public interface CartItemRepository extends JpaRepository<CartItemModel, Integer> {

    List<CartItemModel> findAllByIdInAndCartId(List<Integer> itemIds, Integer cartId);
    List<CartItemModel> findAllByCartId(Integer cartId);
}
