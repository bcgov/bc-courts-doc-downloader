package ca.bc.gov.ag.courts.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "Status", "Host", "Instance" })
public class OrdsHealthResponse {

	@JsonProperty("Status")
	private String status;
	@JsonProperty("Host")
	private String host;
	@JsonProperty("Instance")
	private String instance;

	@JsonProperty("Status")
	public String getStatus() {
		return status;
	}

	@JsonProperty("Status")
	public void setStatus(String status) {
		this.status = status;
	}

	@JsonProperty("Host")
	public String getHost() {
		return host;
	}

	@JsonProperty("Host")
	public void setHost(String host) {
		this.host = host;
	}

	@JsonProperty("Instance")
	public String getInstance() {
		return instance;
	}

	@JsonProperty("Instance")
	public void setInstance(String instance) {
		this.instance = instance;
	}

	@Override
	public String toString() {
		return "[status=" + status + ", host=" + host + ", instance=" + instance + "]";
	}

}