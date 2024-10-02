package ca.bc.gov.ag.courts.Utils;


import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import ca.bc.gov.ag.courts.config.AppProperties;
import jakarta.annotation.PostConstruct;

/**
 * Helper methods for acquiring tokens, setting basic auth headers, etc.
 * 
 * @author 176899
 * 
 */
@Component
public class AuthHelper {
	
	Logger logger = LoggerFactory.getLogger(AuthHelper.class);

    private String clientId;
    private String secretKey;
    private String authority;
    private String msgEndpoint;
    
	private AppProperties props; 
	
	public AuthHelper(AppProperties props) {
		this.props = props; 
	}

    @PostConstruct
    public void init() {
        clientId = props.getMsgClientId();
        authority = props.getMsgAuthority();
        secretKey = props.getMsgSecretKey();
        msgEndpoint = props.getMsgEndpointHost();
    }
    
    /** 
     * 
     * Obtains access token using Client Credential flow (e.g., on behalf of the application and not the user).  
     * 
     * @return
     * @throws MalformedURLException
     * @throws IOException
     * @throws JSONException
     */ 
	public String GetAccessToken() throws Exception {
		
		logger.debug("AuthHelper.GetAccessToken called.");

		String parameters = "client_id="
				+ URLEncoder.encode(this.clientId, java.nio.charset.StandardCharsets.UTF_8.toString())
				+ "&client_secret="
				+ URLEncoder.encode(this.secretKey, java.nio.charset.StandardCharsets.UTF_8.toString()) + "&scope="
				+ URLEncoder.encode(this.msgEndpoint + ".default",
						java.nio.charset.StandardCharsets.UTF_8.toString())
				+ "&grant_type=client_credentials";

		URL url = new URL(this.authority + "oauth2/v2.0/token");
		logger.debug("GetAccessToken calling MS endpoint " + url.toString());
		
		HttpClient client = HttpClient.newHttpClient();
		
		HttpRequest request = HttpRequest.newBuilder()
				  .uri(URI.create(this.authority + "oauth2/v2.0/token"))
				  .POST(BodyPublishers.ofString(parameters))
				  .timeout(Duration.ofSeconds(30))
				  .build();

		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		
		logger.debug("Token response status code: " + response.statusCode());
		logger.debug("Token response body: " + response.body());
		
		JSONObject jResponse = HttpClientHelper.processResponse(response.statusCode(), response.body());
		
		if (jResponse.getInt("responseCode") == HttpStatus.OK.value())  
			return jResponse.getJSONObject("responseMsg").getString("access_token");
		else 
			throw new Exception(jResponse.getJSONObject("responseMsg").getJSONObject("error").getString("message"));

	}
	
	/**
	 * Generates Basic Auth Header for RestTemplate. 
	 * 
	 * @return
	 */
	public static HttpHeaders createBasicAuthHeaders(String userName, String password) {
		return new HttpHeaders() {
			private static final long serialVersionUID = -9217317753759432107L;
			{
				String auth = userName + ":" + password;
				String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
				String authHeader = "Basic " + encodedAuth;
				set("Authorization", authHeader);
			}
		};
	}

}
