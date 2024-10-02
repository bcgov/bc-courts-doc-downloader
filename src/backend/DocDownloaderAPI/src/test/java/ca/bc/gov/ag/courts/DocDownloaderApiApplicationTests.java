package ca.bc.gov.ag.courts;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test") // Sets active profile as 'test'. 
class DocDownloaderApiApplicationTests {
	
	Logger logger = LoggerFactory.getLogger(DocDownloaderApiApplicationTests.class);

	@Test
	void contextLoads() {
		logger.info("Running " + this.getClass().getName());
	}

}
