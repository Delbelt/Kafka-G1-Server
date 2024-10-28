package server.services.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import server.entities.DispatchOrder;
import server.repositories.IDispatchOrderRepository;
import server.services.IDispatchOrderService;

@Service
public class DispatchOrderService implements IDispatchOrderService {
	
	@Autowired
	private IDispatchOrderRepository repository;

	@Override
	public DispatchOrder insertOrUpdate(DispatchOrder dispatchOrder) {
		
		return repository.save(dispatchOrder);
	}
}
