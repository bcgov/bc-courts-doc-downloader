package ca.bc.gov.ag.courts.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "filename", "mimeType" })
public class GetFileResponse {

	@JsonProperty("filename")
	private String filename;
	@JsonProperty("mimeType")
	private String mimeType;

	@JsonProperty("filename")
	public String getFilename() {
		return filename;
	}

	@JsonProperty("filename")
	public void setFilename(String filename) {
		this.filename = filename;
	}

	@JsonProperty("mimeType")
	public String getMimeType() {
		return mimeType;
	}

	@JsonProperty("mimeType")
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

}
