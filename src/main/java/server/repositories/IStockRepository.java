package server.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import server.entities.Stock;

public interface IStockRepository extends JpaRepository<Stock, String> {
	
	@Query("SELECT s FROM Stock s WHERE s.product.code = :codeProduct")
	public Stock findByProduct(String codeProduct);
}
