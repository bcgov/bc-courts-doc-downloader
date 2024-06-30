package ca.bc.gov.ag.courts.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import ca.bc.gov.ag.courts.model.OrdsHealthResponse;
import ca.bc.gov.ag.courts.service.OrdsDocumentServiceImpl;

/**
 * 
 * PingController Unit tests. 
 * 
 * 
 * @author 176899
 *
 */
@SpringBootTest
@ActiveProfiles("test") // Sets active profile as 'test'. Note SecurityConfiguration class disabled for this profile. 
public class PingControllerTests {
	
	Logger logger = LoggerFactory.getLogger(PingControllerTests.class);
	
	private MockMvc mockMvc;
	
	@InjectMocks
	private PingController pingController;
	
	@Mock
	private OrdsDocumentServiceImpl oService;
	
	public PingControllerTests() throws Exception {
		
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(pingController).build();
          
	}
	
    @BeforeEach
    public void setup() {
    	
        OrdsHealthResponse _resp = new OrdsHealthResponse();
        _resp.setStatus("ok");
        
        CompletableFuture<ResponseEntity<OrdsHealthResponse>> ords_future = CompletableFuture.completedFuture(new ResponseEntity<OrdsHealthResponse>(_resp, HttpStatus.OK));
        if (ords_future == null) {
        	System.out.println("ords_future is null");
        }
        when(oService.getOrdsHealth()).thenReturn(ords_future);
    }
	
	@Test
	void contextLoads() {
		logger.info("Context loaded for " + this.getClass().getName());
	}
	
	@DisplayName("GET ping response success - PingController: Ping test")
	@Test
	void pingGet_ok() throws Exception {
	
		// when
        MockHttpServletResponse response = mockMvc.perform(
                get("/ping"))
                .andReturn().getResponse();
        
        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).contains("ok");
		
	}

}
