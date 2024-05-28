package ca.bc.gov.ag.courts.handler;

import java.io.IOException;

import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResponseErrorHandler;

/**
 * 
 * Redis Cache Client Service Error Handler
 * 
 * @author 176899
 *
 */
@Component
public class RedisClientResponseErrorHandler implements ResponseErrorHandler {

	@Override
	public boolean hasError(ClientHttpResponse httpResponse) throws IOException {
		 return httpResponse.getStatusCode().is5xxServerError() || 
		            httpResponse.getStatusCode().is4xxClientError();
	}

	@Override
	public void handleError(ClientHttpResponse httpResponse) throws IOException {
	     throw new HttpClientErrorException(httpResponse.getStatusCode());
	}

}
