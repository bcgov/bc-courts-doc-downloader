package ca.bc.gov.ag.courts.controller;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test") // Sets active profile as 'test'.
public class DocumentControllerTests {
	
	Logger logger = LoggerFactory.getLogger(DocumentControllerTests.class);
	
	@Test
	void contextLoads() {
		logger.info("Running " + this.getClass().getName());
	}

}
