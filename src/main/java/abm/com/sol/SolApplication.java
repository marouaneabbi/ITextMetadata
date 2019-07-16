package abm.com.sol;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class SolApplication {

	private static final Logger log = LoggerFactory.getLogger(SolApplication.class);


	public static void main(String[] args) {
		log.info("SolApplication Application is Starting...");
		try {
			SpringApplication.run(SolApplication.class, args);
		} catch (Exception e) {
			log.error("Error occurred while starting SolApplication");
		}
		log.info("SolApplication Application Started..");
	}


	/**
	 fix size file to upload
	 */


}
