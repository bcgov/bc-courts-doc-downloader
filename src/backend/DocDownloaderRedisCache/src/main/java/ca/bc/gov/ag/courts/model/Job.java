
package ca.bc.gov.ag.courts.model;


import java.io.Serializable;

import org.springframework.data.redis.core.RedisHash;

//import org.springframework.data.redis.core.RedisHash;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.RequiredArgsConstructor;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "id", "correlationId", "guid", "applicationId", "putId", "ordsTimeout", "graphTimeout",
		"graphSessionUrl", "error", "lastErrorMessage", "startDeliveryDtm", "endDeliveryDtm", "percentageComplete", "fileName",
		"mimeType" })
@RedisHash("Job")
@RequiredArgsConstructor
/**
 * 
 * This class represents a document transfer job. It contains all the aspects of the jobs current state and 
 * is stored in the Redis Cache client during the progress from the initial document request to transfer complete.
 * 
 * Note: Guid attribute is a base64 representation of the actual Object Store guid since this string may contain illegal JSON characters. 
 *  
 * @author 176899
 *
 */
public class Job implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6588653204556249341L;
	
	
	@JsonProperty("id")
	private String id;
	@JsonProperty("guid")
	private String guid;
	@JsonProperty("applicationId")
	private String applicationId;
	@JsonProperty("putId")
	private String putId;
	@JsonProperty("ordsTimeout")
	private Boolean ordsTimeout;
	@JsonProperty("graphTimeout")
	private Boolean graphTimeout;
	@JsonProperty("graphSessionUrl")
	private String graphSessionUrl;
	@JsonProperty("error")
	private Boolean error;
	@JsonProperty("lastErrorMessage")
	private String lastErrorMessage;
	@JsonProperty("startDeliveryDtm")
	private String startDeliveryDtm;
	@JsonProperty("endDeliveryDtm")
	private String endDeliveryDtm;
	@JsonProperty("percentageComplete")
	private Integer percentageComplete;
	@JsonProperty("fileName")
	private String fileName;
	@JsonProperty("mimeType")
	private String mimeType;
	
	@JsonProperty("id")
	public String getId() {
		return id;
	}

	@JsonProperty("id")
	public void setId(String id) {
		this.id = id;
	}

	@JsonProperty("guid")
	public String getGuid() {
		return guid;
	}

	@JsonProperty("guid")
	public void setGuid(String guid) {
		this.guid = guid;
	}

	@JsonProperty("applicationId")
	public String getApplicationId() {
		return applicationId;
	}

	@JsonProperty("applicationId")
	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	@JsonProperty("putId")
	public String getPutId() {
		return putId;
	}

	@JsonProperty("putId")
	public void setPutId(String putId) {
		this.putId = putId;
	}

	@JsonProperty("ordsTimeout")
	public Boolean getOrdsTimeout() {
		return ordsTimeout;
	}

	@JsonProperty("ordsTimeout")
	public void setOrdsTimeout(Boolean ordsTimeout) {
		this.ordsTimeout = ordsTimeout;
	}

	@JsonProperty("graphTimeout")
	public Boolean getGraphTimeout() {
		return graphTimeout;
	}

	@JsonProperty("graphTimeout")
	public void setGraphTimeout(Boolean graphTimeout) {
		this.graphTimeout = graphTimeout;
	}

	@JsonProperty("graphSessionUrl")
	public String getGraphSessionUrl() {
		return graphSessionUrl;
	}

	@JsonProperty("graphSessionUrl")
	public void setGraphSessionUrl(String graphSessionUrl) {
		this.graphSessionUrl = graphSessionUrl;
	}

	@JsonProperty("error")
	public Boolean getError() {
		return error;
	}

	@JsonProperty("error")
	public void setError(Boolean error) {
		this.error = error;
	}

	@JsonProperty("lastErrorMessage")
	public String getLastErrorMessage() {
		return lastErrorMessage;
	}

	@JsonProperty("lastErrorMessage")
	public void setLastErrorMessage(String lastErrorMessage) {
		this.lastErrorMessage = lastErrorMessage;
	}

	@JsonProperty("startDeliveryDtm")
	public String getStartDeliveryDtm() {
		return startDeliveryDtm;
	}

	@JsonProperty("startDeliveryDtm")
	public void setStartDeliveryDtm(String startDeliveryDtm) {
		this.startDeliveryDtm = startDeliveryDtm;
	}

	@JsonProperty("endDeliveryDtm")
	public String getEndDeliveryDtm() {
		return endDeliveryDtm;
	}

	@JsonProperty("endDeliveryDtm")
	public void setEndDeliveryDtm(String endDeliveryDtm) {
		this.endDeliveryDtm = endDeliveryDtm;
	}

	@JsonProperty("percentageComplete")
	public Integer getPercentageComplete() {
		return percentageComplete;
	}

	@JsonProperty("percentageComplete")
	public void setPercentageComplete(Integer percentageComplete) {
		this.percentageComplete = percentageComplete;
	}

	@JsonProperty("fileName")
	public String getFileName() {
		return fileName;
	}

	@JsonProperty("fileName")
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	@JsonProperty("mimeType")
	public String getMimeType() {
		return mimeType;
	}

	@JsonProperty("mimeType")
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	@Override
	public String toString() {
		return "Job [id=" + id + ", guid=" + guid + ", applicationId=" + applicationId + ", putId=" + putId
				+ ", ordsTimeout=" + ordsTimeout + ", graphTimeout=" + graphTimeout
				+ ", graphSessionUrl=" + graphSessionUrl + ", error=" + error + ", lastErrorMessage=" + lastErrorMessage
				+ ", startDeliveryDtm=" + startDeliveryDtm + ", endDeliveryDtm=" + endDeliveryDtm
				+ ", percentageComplete=" + percentageComplete + ", fileName=" + fileName + ", mimeType=" + mimeType
				+ "]";
	}

}