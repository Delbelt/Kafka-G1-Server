package server.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderItemDTO {

	@JsonProperty("idOrderItem")
	private int idOrderItem;	

	private String code;

	private String color;

	private String size;

	private int quantity;
}
