package spring.practice.elmenus_lite.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import spring.practice.elmenus_lite.model.auditing.AuditingFields;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "order_item")
@Getter
@Setter
@ToString
public class OrderItemModel extends AuditingFields implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "order_id", nullable = false)
	private OrderModel order;

	@ManyToOne(optional = false)
	@JoinColumn(name = "menu_item_id", nullable = false)
	private MenuItemModel menuItem;

	@Min(value = 1, message = "Quantity must be at least 1")
	@Column(nullable = false)
	private Integer quantity;

	@Column(nullable = false)
	private BigDecimal unitPrice;

	@Column(nullable = false)
	private BigDecimal subtotal;

	private String specialInstructions;

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		OrderItemModel that = (OrderItemModel) o;
		return Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
