package server.entities;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name="DispatchOrder")
@Data
public class DispatchOrder {

	@Id
	@Column(name="dispatchOrder_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int dispatchOrder_id;
	
	@Column(name="purchaseOrder_id")
	private int purchaseOrder_id;
	
	@Column(name="estimatedDate", nullable=true, columnDefinition = "DATE")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate estimatedDate;

	public DispatchOrder(int purchaseOrder_id, LocalDate estimatedDate) {
		super();
		this.purchaseOrder_id = purchaseOrder_id;
		this.estimatedDate = estimatedDate;
	}	
}
