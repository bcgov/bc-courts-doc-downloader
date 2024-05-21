package ca.bc.gov.ag.courts.Utils;


import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.support.RetrySynchronizationManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import ca.bc.gov.ag.courts.config.AppProperties;
import jakarta.annotation.PostConstruct;

/**
 * Helper methods for acquiring tokens, etc.
 */
@Component
public class AuthHelper {
	
	Logger logger = LoggerFactory.getLogger(AuthHelper.class);

    private String clientId;
    private String secretKey;
    private String authority;
    
    private final String emailRegex = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@" 
	        + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
    
    
    @Autowired
	AppProperties props; 
    
    Pattern emailPattern;  

    @PostConstruct
    public void init() {
        clientId = props.getMsgClientId();
        authority = props.getMsgAuthority();
        secretKey = props.getMsgSecretKey();
        
        emailPattern = Pattern.compile(emailRegex);
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
	public CompletableFuture<JSONObject> GetAccessToken() throws MalformedURLException, IOException, JSONException {

		String parameters = "client_id="
				+ URLEncoder.encode(this.clientId, java.nio.charset.StandardCharsets.UTF_8.toString())
				+ "&client_secret="
				+ URLEncoder.encode(this.secretKey, java.nio.charset.StandardCharsets.UTF_8.toString()) + "&scope="
				+ URLEncoder.encode("https://graph.microsoft.com/.default",
						java.nio.charset.StandardCharsets.UTF_8.toString())
				+ "&grant_type=client_credentials";

		HttpURLConnection connection = null;
		URL url = new URL(this.authority + "/oauth2/v2.0/token");
		connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		connection.setRequestProperty("Content-Length", "" + Integer.toString(parameters.getBytes().length));
		connection.setDoOutput(true);
		connection.connect();

		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
		out.write(parameters);
		out.close();

		String response = HttpClientHelper.getResponseStringFromConn(connection);
		int responseCode = connection.getResponseCode();

		if (responseCode >= 400) {
			logger.error("Token request failure. Response: " + response);
		} else {
			logger.debug("Token request response: " + response);
		}

		return CompletableFuture.completedFuture(HttpClientHelper.processResponse(responseCode, response));

	}

}
