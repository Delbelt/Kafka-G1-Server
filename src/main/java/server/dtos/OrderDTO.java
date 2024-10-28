package server.dtos;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
	
	private String store_code;
	private int order_id;
	private List<OrderItemDTO> items;
	private LocalDateTime requestDate;
}
