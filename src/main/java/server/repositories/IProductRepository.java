package server.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import server.entities.Product;

public interface IProductRepository extends JpaRepository<Product, String>  {

}
