package ca.bc.gov.ag.courts.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "objcenords status" })
public class OrdsHealthResponse {

	@JsonProperty("objcenords status")
	private String status;


	@JsonProperty("objcenords status")
	public String getStatus() {
		return status;
	}

	@JsonProperty("objcenords status")
	public void setStatus(String status) {
		this.status = status;
	}

}