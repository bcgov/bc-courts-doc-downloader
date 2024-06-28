package ca.bc.gov.ag.courts.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import ca.bc.gov.ag.courts.Utils.MinioS3Utils;
import ca.bc.gov.ag.courts.config.AppProperties;
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
	
	
	public DocumentControllerTests() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(documentController).build();
        
        when(appProps.getOrdsApplicationId()).thenReturn("1234");
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
	
	@DisplayName("POST success - DocumentController: : Doc Status test")
	@Test
	void documentStatusTransferIdGet_ok() {
		
	}
	
	@DisplayName("POST success - DocumentController: : Doc Terminate test")
	@Test
	void documentTerminatePost_ok() {
		
	}

}
