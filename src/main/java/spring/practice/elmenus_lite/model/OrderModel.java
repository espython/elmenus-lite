package spring.practice.elmenus_lite.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import spring.practice.elmenus_lite.model.auditing.AuditingFields;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "orders")
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class OrderModel extends AuditingFields implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(nullable = false, unique = true)
	private String orderNumber;

	@ManyToOne
	@JoinColumn(name = "customer_id", nullable = false)
	private CustomerModel customer;

	@ManyToOne
	@JoinColumn(name = "restaurant_id", nullable = false)
	private RestaurantModel restaurant;

	@ManyToOne
	@JoinColumn(name = "status_id", nullable = false)
	private OrderStatusModel status;

	@ManyToOne
	@JoinColumn(name = "delivery_address_id", nullable = false)
	private AddressModel deliveryAddress;

	@Column(nullable = false)
	private BigDecimal subtotal;

	@Column(nullable = false)
	private BigDecimal deliveryFee;

	@Column(nullable = false)
	private BigDecimal tax;

	@Column(nullable = false)
	private BigDecimal total;

	private String specialInstructions;

	@Column(nullable = false)
	private LocalDateTime estimatedDeliveryTime;

	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
	private Set<OrderItemModel> orderItems = new HashSet<>();
}
