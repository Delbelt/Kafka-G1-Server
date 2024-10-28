package server.services;

import java.util.List;

import server.entities.Stock;

public interface IStockService {
	
	public Stock findByProduct(String codeProduct);

	public List<Stock> massiveInsertOrUpdate(List<Stock> stocks);
	
	public Stock insertOrUpdate(Stock stock);
}
