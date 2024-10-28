package server.services.implementations;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import server.entities.PurchaseOrder;
import server.repositories.IPurchaseOrderRepository;
import server.services.IPurchaseOrderService;

@Service
public class PurchaseOrderService implements IPurchaseOrderService {
	
	@Autowired
	private IPurchaseOrderRepository repository;

	@Override
	public PurchaseOrder insertOrUpdate(PurchaseOrder order) {
		
		return repository.save(order);
	}

	@Override
	public List<PurchaseOrder> getAllFromState(String state) {	
		
		return repository.getAllFromState(state);
	}

	@Override
	public List<PurchaseOrder> getAllFromStateAndProduct(String state, String codeProduct) {
		
		return repository.getAllFromStateAndProduct(state, codeProduct);
	}
}
