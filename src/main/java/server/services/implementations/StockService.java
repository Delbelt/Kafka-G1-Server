package server.services.implementations;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import server.entities.PurchaseOrder;
import server.entities.Stock;
import server.kafka.consumer.KafkaMessageConsumer;
import server.kafka.utils.StatePurchaseOrder;
import server.repositories.IStockRepository;
import server.services.IPurchaseOrderService;
import server.services.IStockService;

@Service
public class StockService implements IStockService {
	
	@Autowired
	private IStockRepository repository;
	
	@Autowired
	private IPurchaseOrderService purchaseOrderService;
	
	@Autowired
	private KafkaMessageConsumer consumer;
	
	@Override	
	public Stock findByProduct(String codeProduct) {

		var response = repository.findByProduct(codeProduct);

		return response;
	}
	
	@Override
	@Transactional
	public List<Stock> massiveInsertOrUpdate(List<Stock> stocks) {
		
		return repository.saveAll(stocks);
	}

	@Override
	public Stock insertOrUpdate(Stock stock) {
		
		var response = repository.save(stock);
		
		// Cuando el proveedor agrega un stock, se reprocesan las ordenes que tengan dicho producto.
		
		var order = purchaseOrderService.getAllFromStateAndProduct(StatePurchaseOrder.PAUSED, stock.getCode());
				
		for (PurchaseOrder purchaseOrder : order) {
			
			consumer.reprocessPausedOrders(purchaseOrder.getItems());	
		}		
		
		return response;
	}
}
