package ca.bc.gov.ag.courts.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;

import ca.bc.gov.ag.courts.config.AppProperties;
import ca.bc.gov.ag.courts.handler.GenericErrorHandler;
import ca.bc.gov.ag.courts.service.RedisCacheClientServiceImpl;

/**
 * 
 * Redis Cache Client Service Unit tests.
 * 
 * 
 * @author 176899
 *
 */
@SpringBootTest
@ActiveProfiles("test") // Sets active profile as 'test'. Note SecurityConfiguration class disabled for this profile.
public class RedisCacheClientServiceTests {

	Logger logger = LoggerFactory.getLogger(RedisCacheClientServiceTests.class);

	private MockMvc mockMvc;

	@InjectMocks
	private RedisCacheClientServiceImpl redisCacheClient;
	 
	@Mock
	private Environment environment;
	
	@Mock
	private RestTemplateBuilder restTemplateBuilder;
	
	@Mock
	private RestTemplate restTemplate;
	
	@Mock
	private AppProperties props; 
	
    @BeforeEach
    public void setup() {
    	
//    	// Mock RestTemplateBuilder behaviour
//        doReturn(this.restTemplateBuilder).when(this.restTemplateBuilder).basicAuthentication(anyString(), anyString());
//        doReturn(this.restTemplateBuilder).when(this.restTemplateBuilder).rootUri(anyString());
//        doReturn(this.restTemplateBuilder).when(this.restTemplateBuilder).errorHandler(new GenericErrorHandler());
//        doReturn(this.restTemplate).when(this.restTemplateBuilder).build();
//    	
//    	MockitoAnnotations.openMocks(this);
//		this.mockMvc = MockMvcBuilders.standaloneSetup(redisCacheClient).build();
//		
//        
//        
//        // Mock RestTemplateBuilder behaviour
//        when(this.environment.getActiveProfiles()).thenReturn(new String[] {"test"});
//		
//        when(this.props.getRedisClientUsername()).thenReturn("user");
//        when(this.props.getRedisClientPassword()).thenReturn("password");
           
    }
	
//	@Test
//	void contextLoads() {
//		logger.info("Context loaded for " + this.getClass().getName());
//	}

}
