package server.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import server.entities.PurchaseOrder;

public interface IPurchaseOrderRepository extends JpaRepository<PurchaseOrder, Integer> {
	
	@Query("SELECT po FROM PurchaseOrder po LEFT JOIN FETCH po.items WHERE po.state = :state")
    List<PurchaseOrder> getAllFromState(String state);
	
	@Query("""
		    SELECT DISTINCT po
		    FROM PurchaseOrder po
		    JOIN FETCH po.items items
		    WHERE po.state = :state 
		      AND EXISTS (
		          SELECT 1 
		          FROM OrderItem oi 
		          WHERE oi MEMBER OF po.items 
		            AND oi.code = :codeProduct
		      )
		""")
    List<PurchaseOrder> getAllFromStateAndProduct(String state, String codeProduct);
}
