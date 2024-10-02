package ca.bc.gov.ag.courts.service;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import ca.bc.gov.ag.courts.Utils.AuthHelper;
import ca.bc.gov.ag.courts.config.AppProperties;

/**
 * 
 * Job Service Unit tests. 
 * 
 * 
 * @author 176899
 *
 */
@SpringBootTest
@ActiveProfiles("test") // Sets active profile as 'test'. Note SecurityConfiguration class disabled for this profile. 
public class JobServiceImplTests {
	
	Logger logger = LoggerFactory.getLogger(JobServiceImplTests.class);
	
	private MockMvc mockMvc;
	
	@InjectMocks
	private JobServiceImpl jobService;
	
	@Mock	
	private RedisCacheClientService rService; 
	
	@Mock	
	private OrdsDocumentService oService;
	
	@Mock	
	private AuthHelper aService; 
	
	@Mock	
	private MSGraphService mService; 
	
	@Mock	
	private S3PollerService pService; 
	
	@Mock	
	private S3Service sService;
	
	@Mock	
	private AppProperties props; 
	
	
	@Test
	void contextLoads() {
		logger.info("Context loaded for " + this.getClass().getName());
	}

}
