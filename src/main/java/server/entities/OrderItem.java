package server.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data 
@Table(name="order_item")
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonProperty("idOrderItem")
	private int idOrderItem;

	@Column(name="code", nullable=false)
	private String code;
	
	@Column(name="color", nullable=false)
	private String color;
	
	@Column(name="size", nullable=false)
	private String size;
	
	@Column(name="quantity", nullable=false)
	private int quantity;
}
