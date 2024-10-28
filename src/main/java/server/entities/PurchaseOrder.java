package server.entities;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data 
@Table(name="purchase_order")
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrder {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int order_id;
	
	@Column(name="state", nullable=false)
	private String state;
	
	@Column(name="store_code", nullable=false)
	private String store_code;
	
	@OneToMany(cascade=CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name="order_id")
	private List<OrderItem> items;
	
	@Column(name="requestDate", nullable=false, columnDefinition = "DATETIME")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
	private LocalDateTime requestDate;
	
	@Column(name="ReceiptDate", nullable=true, columnDefinition = "DATETIME")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
	private LocalDateTime ReceiptDate;

	public PurchaseOrder(int order_id, String state, String store_code, List<OrderItem> items,
			LocalDateTime requestDate) {
		super();
		this.order_id = order_id;
		this.state = state;
		this.store_code = store_code;
		this.items = items;
		this.requestDate = requestDate;
	}	
}
