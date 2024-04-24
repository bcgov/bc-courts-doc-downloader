package ca.bc.gov.ag.courts.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "ilename", "MimeType" })
public class GetFileResponse {

	@JsonProperty("Filename")
	private String filename;
	@JsonProperty("MimeType")
	private String mimeType;

	@JsonProperty("Filename")
	public String getFilename() {
		return filename;
	}

	@JsonProperty("Filename")
	public void setFilename(String filename) {
		this.filename = filename;
	}

	@JsonProperty("MimeType")
	public String getMimeType() {
		return mimeType;
	}

	@JsonProperty("MimeType")
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

}
