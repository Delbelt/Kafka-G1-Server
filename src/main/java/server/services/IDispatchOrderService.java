package server.services;

import server.entities.DispatchOrder;

public interface IDispatchOrderService {
	
	public DispatchOrder insertOrUpdate(DispatchOrder dispatchOrder);
}
