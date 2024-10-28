package server.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import server.dtos.OrderDTO;
import server.entities.DispatchOrder;
import server.entities.OrderItem;
import server.entities.PurchaseOrder;
import server.entities.Stock;
import server.kafka.utils.StatePurchaseOrder;
import server.kafka.utils.TopicsKafka;
import server.services.IDispatchOrderService;
import server.services.IPurchaseOrderService;
import server.services.IStockService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaMessageConsumer {
	
	@Autowired
	private IStockService stockService;
	
	@Autowired
	private IDispatchOrderService dispatchOrderService;
	
	@Autowired
	private IPurchaseOrderService purchaseOrderService;

	private final ObjectMapper objectMapper = new ObjectMapper();

//	@KafkaListener(topics = TopicsKafka.PURCHASE_ORDER, groupId = TopicsKafka.groupIdTopics)
	public void consumePurchaseOrder(String message) {		
		
		// y enviar al topic de /{codigo de tienda}/solicitudes	
		
		try {	
			
			objectMapper.registerModule(new JavaTimeModule());
			
			PurchaseOrder order = objectMapper.readValue(message, PurchaseOrder.class);
		
			System.out.println(order);
			
			boolean isRejected = isTopicRejected(order.getItems());
			
			if(!isRejected) {
				
				isTopicPaused(order.getItems());
			}
		}
		
		catch (Exception e) {
			
			System.err.println("Error al deserializar el mensaje: " + e.getMessage());
		}
	}
	
//	@KafkaListener(topics = TopicsKafka.RECEPTION, groupId = TopicsKafka.groupIdTopics)
	public void consumeReception(String message) {
		
		try {	
			
			objectMapper.registerModule(new JavaTimeModule());
			
			PurchaseOrder order = objectMapper.readValue(message, PurchaseOrder.class);
		
			System.out.println(order);
			
			// Actualiza la orden de compra
			purchaseOrderService.insertOrUpdate(order);
		} 
		
		catch (Exception e) {
			
			System.err.println("Error al deserializar el mensaje: " + e.getMessage());
		}
	}
	
	public boolean isTopicRejected(List<OrderItem> items) {

		// Lista parcial de las observaciones de la orden de compra
		List<String> observations = new ArrayList<>();

		boolean isTopicRejected = false;

		for (OrderItem item : items) { // Se verifica los items que solicita la tienda

			var stock = stockService.findByProduct(item.getCode()); // Para verificar si existe el producto

			if (stock == null) {

				String errorMessage = "Articulo [" + item.getCode() + "]: no existe.";

				observations.add(errorMessage);
			}

			else {

				if (item.getQuantity() < 1) { // En caso de que exista, se verifica que este la cantidad bien informada.

					String errorMessage = "Articulo [" + item.getCode() + "]: cantidad mal informada.";

					observations.add(errorMessage);
				}
			}
		}

		if (observations.size() > 0) { // Si hay observaciones tiene que rechazarse.

			System.out.println("**********************************");
			System.out.println(observations);
			System.out.println("**********************************");

			isTopicRejected = true; // Se rechaza la orden de compra
			
			LocalDateTime requestDate = LocalDateTime.now();
			
			// Todos los items son de la misma orden.
			int order_id = items.get(0).getIdOrderItem();

			// Se agrega la orden de compra rechazada para tenerla en el historial
			 PurchaseOrder order = new PurchaseOrder(order_id, StatePurchaseOrder.REJECTED, "AAA111", items, requestDate);
			 
			 var responseOrder = purchaseOrderService.insertOrUpdate(order);

			// ENVIAR TOPICO RECHAZADO();
		}

		return isTopicRejected;
	}	
	
	public boolean isTopicPaused(List<OrderItem> items) {

		// Lista parcial de las observaciones de la orden de compra
		List<String> observations = new ArrayList<>();

		// Lista parcial para modificar los stock en caso de ser aprobada en su totalidad
		List<Stock> stocksProviderToModify = new ArrayList<>();

		boolean isTopicPaused = false;

		for (OrderItem item : items) {

			var stock = stockService.findByProduct(item.getCode()); // Traigo el stock del proveedor

			boolean hasStock = stock.getQuantity() > item.getQuantity(); // Verifico si alcanza el stock

			if (hasStock) {

				stock.setQuantity(stock.getQuantity() - item.getQuantity()); // Se resta el stock del proveedor
				stocksProviderToModify.add(stock); // Se agrega a la lista que hay que modificar en caso de ser aprobada la compra
			}

			else { // En caso de que no alcance el stock

				String observation = "Articulo: [" + item.getCode() + "]: tiene faltante de stock";

				observations.add(observation); // Se agrega la observacion a la lista para comunicarle a la tienda
			}
		}

		boolean completeOrder = observations.size() < 1; // Si no tiene observaciones significa que esta completa

		if (completeOrder) {

			LocalDateTime requestDate = LocalDateTime.now();
			LocalDate estimatedDate = LocalDate.now(); // TODO: Se podria modificar con un metodo que calcule la logistica

			// Se persiste la orden de compra aprobada en el historial.
			PurchaseOrder order = new PurchaseOrder(38, StatePurchaseOrder.ACCEPTED, "AAA111", items, requestDate);

			// Se crea la orden de despacho para generar el id y comunicarle a la tienda para que modifique los cambios.
			DispatchOrder requestDispatch = new DispatchOrder(order.getOrder_id(), estimatedDate);

			// 1: Genera una orden de despacho asociada a la orden de compra
			// var responseDispatch = dispatchOrderService.insertOrUpdate(requestDispatch);

			// 2: Se persiste la orden de compra aprobada
			// var responseOrder = purchaseOrderService.insertOrUpdate(order);

			// 3: Se descuenta el stock del proveedor utilizando la lista parcial que se modifico.
			// var stockModify = stockService.massiveInsertOrUpdate(stocksProviderToModify);

			// 4: ENVIAR TOPICO ACEPTADO();
			// ¿Que es lo que necesito enviar en caso de que se acepte la peticion?

			// 5: ENVIAR TOPICO DESPACHADO();
			// Con la siguiente información: id de la orden de despacho, id de la orden de compra, fecha estimada de envío.

			System.out.println("**********************************");
			System.out.println("******** ORDEN COMPLETADA ********");
			System.out.println("**********************************");
		}

		else { // Si hay observaciones tiene que pausarse.

			System.out.println("**********************************");
			System.out.println(observations);
			System.out.println("**********************************");

			isTopicPaused = true; // La orden de compra se pausa internamente.
			LocalDateTime requestDate = LocalDateTime.now();

			// 1: Se persiste la orden de compra pausada para su posterior tratamiento (cuando se modifique el stock)
			PurchaseOrder order = new PurchaseOrder(38, StatePurchaseOrder.PAUSED, "AAA111", items, requestDate);
//			var responseOrder = purchaseOrderService.insertOrUpdate(order);

			System.out.println("**********************************");
			System.out.println("********* ORDEN PAUSADA **********");
			System.out.println("**********************************");

			// 2: ENVIAR TOPIC PAUSADO();
		}

		return isTopicPaused;
	}
	
	public boolean reprocessPausedOrders(List<OrderItem> items) {
		
		// Lista parcial de las observaciones de la orden de compra
		List<String> observations = new ArrayList<>();
		
		// Lista parcial para modificar los stock en caso de ser aprobada en su totalidad
		List<Stock> stocksProviderToModify = new ArrayList<>();

		boolean isTopicPaused = false;

		for (OrderItem item : items) {

			var stock = stockService.findByProduct(item.getCode()); // Traigo el stock del proveedor

			boolean hasStock = stock.getQuantity() > item.getQuantity(); // Verifico si alcanza el stock

			if (hasStock) {

				stock.setQuantity(stock.getQuantity() - item.getQuantity()); // Se resta el stock del proveedor
				stocksProviderToModify.add(stock); // Se agrega a la lista que hay que modificar en caso de ser aprobada la compra
			}

			else { // En caso de que no alcance el stock

				String observation = "Articulo: [" + item.getCode() + "]: tiene faltante de stock";

				observations.add(observation); // Se agrega la observacion a la lista para comunicarle a la tienda
			}
		}

		boolean completeOrder = observations.size() < 1; // Si no tiene observaciones significa que esta completa

		if (completeOrder) {

			LocalDateTime requestDate = LocalDateTime.now();
			LocalDate estimatedDate = LocalDate.now(); // TODO: Se podria modificar con un metodo que calcule la logistica

			// Se persiste la orden de compra aprobada en el historial.
			PurchaseOrder order = new PurchaseOrder(38, StatePurchaseOrder.ACCEPTED, "AAA111", items, requestDate);

			// Se crea la orden de despacho para generar el id y comunicarle a la tienda para que modifique los cambios.
			DispatchOrder requestDispatch = new DispatchOrder(order.getOrder_id(), estimatedDate);

			// 1: Genera una orden de despacho asociada a la orden de compra
			// var responseDispatch = dispatchOrderService.insertOrUpdate(requestDispatch);

			// 2: Se persiste la orden de compra aprobada
			// var responseOrder = purchaseOrderService.insertOrUpdate(order);

			// 3: Se descuenta el stock del proveedor utilizando la lista parcial que se modifico.
			// var stockModify = stockService.massiveInsertOrUpdate(stocksProviderToModify);

			// 4: ENVIAR TOPICO ACEPTADO();
			// ¿Que es lo que necesito enviar en caso de que se acepte la peticion?

			// 5: ENVIAR TOPICO DESPACHADO();
			// Con la siguiente información: id de la orden de despacho, id de la orden de compra, fecha estimada de envío.

			System.out.println("**********************************");
			System.out.println("******** ORDEN COMPLETADA ********");
			System.out.println("**********************************");
		}

		else { // Si hay observaciones sigue pausada internamente
			
			System.out.println("**********************************");
			System.out.println("********* SIGUE PAUSADA **********");
			System.out.println("**********************************");

			isTopicPaused = true; // La orden sigue pausada
		}

		return isTopicPaused;
	}
}
