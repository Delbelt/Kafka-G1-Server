package server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class ServerKafkaApplication {

	public static void main(String[] args) {

		ApplicationContext context = SpringApplication.run(ServerKafkaApplication.class, args);
		
//		IOrderProcessingService processPurchaseOrder = context.getBean(IOrderProcessingService.class);	
	}
}
