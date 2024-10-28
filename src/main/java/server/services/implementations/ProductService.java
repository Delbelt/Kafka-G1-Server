package server.services.implementations;

import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;

import server.entities.Product;
import server.kafka.KafkaMessageProducer;
import server.kafka.utils.TopicsKafka;
import server.repositories.IProductRepository;
import server.services.IProductService;

public class ProductService implements IProductService {
	
	@Autowired
	private IProductRepository repository;
	
	private final KafkaMessageProducer kafkaMessageProducer;

    @Autowired
    public ProductService(KafkaMessageProducer kafkaMessageProducer) {
        this.kafkaMessageProducer = kafkaMessageProducer;
    }

	@Override
	public boolean inserTopic(Product product) {
	
		boolean insertTopic = false;
		
		try {
			
			var response = repository.save(product);
			
			// Se agrega al topico de novedades
			kafkaMessageProducer.sendObjectMessage(TopicsKafka.NEWS, response);
			
			insertTopic = true;
		}
		
		catch (InterruptedException | ExecutionException e) {
			
			System.out.println(e.getMessage());
		}
		
		return insertTopic;
	}
}
