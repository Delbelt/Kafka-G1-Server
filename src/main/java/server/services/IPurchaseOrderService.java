package server.services;

import java.util.List;

import server.entities.PurchaseOrder;

public interface IPurchaseOrderService {
	
	public PurchaseOrder insertOrUpdate(PurchaseOrder order);
	
	public List<PurchaseOrder> getAllFromState(String state);
	
	public List<PurchaseOrder> getAllFromStateAndProduct(String state, String codeProduct);
}
