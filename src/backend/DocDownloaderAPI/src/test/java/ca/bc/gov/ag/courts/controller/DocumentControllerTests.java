package ca.bc.gov.ag.courts.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import ca.bc.gov.ag.courts.Utils.MinioS3Utils;
import ca.bc.gov.ag.courts.config.AppProperties;
import ca.bc.gov.ag.courts.model.Job;
import ca.bc.gov.ag.courts.service.JobServiceImpl;
import ca.bc.gov.ag.courts.service.RedisCacheClientServiceImpl;
import ca.bc.gov.ag.courts.service.S3PollerService;
import ca.bc.gov.ag.courts.service.S3ServiceImpl;

@SpringBootTest
@ActiveProfiles("test") // Sets active profile as 'test'. Note SecurityConfiguration class disabled for this profile. 
public class DocumentControllerTests {
	
	Logger logger = LoggerFactory.getLogger(DocumentControllerTests.class);
	
	private MockMvc mockMvc;
	
	@InjectMocks
	private DocumentController documentController;
	
	@Mock
	private AppProperties appProps;
	
	@Mock
	private JobServiceImpl jService; 
	
	@Mock 
	private RedisCacheClientServiceImpl rService;
	
	@Mock
	private S3PollerService pService;
	
	@Mock
	private S3ServiceImpl sService;
	
	@Mock
	private MinioS3Utils mUtils;
	
	private final String DocUploadGoodRequestBody = "{\"objGuid\":\"d2x3dGJpZ3RYUWA7bSo0NExpRlVBYk5nNTRyPEpfdV55U2dHbWpVQysyOHw+dXZOMENUYGxLSi54azB8P1JSRHFIY1t1aS4wODQyNDUwMDAuMzQyNjE2LjI0NjA0MTcuV1h7Xw==\",\"email\":\"someguy@bccourts.ca\",\"filePath\":\"/scvtest/case2/\"}";
	private final String DocUploadPoorRequestBody = "{\"email\":\"someguy@bccourts.ca\",\"filePath\":\"/scvtest/case2/\"}";
	
	public DocumentControllerTests() throws Exception {
		
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(documentController).build();
        
        when(appProps.getOrdsApplicationId()).thenReturn("1234");
        
        // Set up job to return for JobService. 
        Job job = new Job();
		job.setId("1234");    
		job.setGuid("guid"); 
		job.setApplicationId(appProps.getOrdsApplicationId());
		job.setGraphSessionUrl(null);
		job.setEmail("someguy@place.com");
		job.setError(false);
		job.setLastErrorMessage(null);
		job.setStartDeliveryDtm(null);
		job.setEndDeliveryDtm(null);
		job.setPercentageComplete(0); 
		job.setFilePath("/scvtest/case2/");
		job.setFileName("myfile.pdf"); 
		job.setMimeType("application/pdf"); 
        
        CompletableFuture<ResponseEntity<Job>> resp = CompletableFuture.completedFuture(new ResponseEntity<Job>(job, HttpStatus.OK)); 
        when(rService.getJob("12345")).thenReturn(resp);
        
	}
	
	
    @BeforeEach
    public void setup() {
        //TODO - if required. 
    }
	
	@Test
	void contextLoads() {
		logger.info("Context loaded for " + this.getClass().getName());
	}
	
	@DisplayName("POST success - DocumentController: Upload test")
	@Test
	void documentUploadPost_ok() throws Exception {
		
		// when
        MockHttpServletResponse response = mockMvc.perform(
                post("/document/upload")
                	.content(DocUploadGoodRequestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        
        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.ACCEPTED.value());
        assertThat(response.getContentAsString()).contains("transferId");
		
	}
	
	@DisplayName("POST failure - DocumentController: Upload test")
	@Test
	void documentUploadPost_bad() throws Exception {
		
		// when
        MockHttpServletResponse response = mockMvc.perform(
                post("/document/upload")
                	.content(DocUploadPoorRequestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        
        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
		
	}
	
	@DisplayName("GET success - DocumentController: : Doc Status test")
	@Test
	void documentStatusTransferIdGet_ok() throws Exception {
		
		// when
        MockHttpServletResponse response = mockMvc.perform(
                get("/document/status/12345"))
                .andReturn().getResponse();
        
        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).contains("transferId");
		
	}
	
	@DisplayName("GET failure - DocumentController: : Doc Status test")
	@Test
	void documentStatusTransferIdGet_bad() throws Exception {
	
		//TODO - check this one. Need to validate 404 when transferId not found. 
		
        mockMvc.perform(
                get("/document/status/"))
                .andExpect(status().isNotFound());
	}
	
	@DisplayName("POST success - DocumentController: : Doc Terminate test")
	@Test
	void documentTerminatePost_ok() {
		
	}

}
