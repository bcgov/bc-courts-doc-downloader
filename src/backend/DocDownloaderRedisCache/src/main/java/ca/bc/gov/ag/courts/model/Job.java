package ca.bc.gov.ag.courts.model;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"id", "guid", "applicationId", "putId", "ordsTimeout", "graphTimeout", "checksum",
        "graphSessionId", "error", "lastErrorMessage", "startDelivery", "endDelivery", "percentageComplete", "fileName",
        "mimeType"})
@RedisHash("Job")
@RequiredArgsConstructor
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
    @JsonProperty("checksum")
    private String checksum;
    @JsonProperty("graphSessionId")
    private String graphSessionId;
    @JsonProperty("error")
    private Boolean error;
    @JsonProperty("lastErrorMessage")
    private String lastErrorMessage;
    @JsonProperty("startDelivery")
    private String startDelivery;
    @JsonProperty("endDelivery")
    private String endDelivery;
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

    @JsonProperty("checksum")
    public String getChecksum() {
        return checksum;
    }

    @JsonProperty("checksum")
    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    @JsonProperty("graphSessionId")
    public String getGraphSessionId() {
        return graphSessionId;
    }

    @JsonProperty("graphSessionId")
    public void setGraphSessionId(String graphSessionId) {
        this.graphSessionId = graphSessionId;
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

    @JsonProperty("startDelivery")
    public String getStartDelivery() {
        return startDelivery;
    }

    @JsonProperty("startDelivery")
    public void setStartDelivery(String startDeliveryDtm) {
        this.startDelivery = startDeliveryDtm;
    }

    @JsonProperty("endDelivery")
    public String getEndDelivery() {
        return endDelivery;
    }

    @JsonProperty("endDelivery")
    public void setEndDelivery(String endDeliveryDtm) {
        this.endDelivery = endDeliveryDtm;
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
                + ", ordsTimeout=" + ordsTimeout + ", graphTimeout=" + graphTimeout + ", checksum=" + checksum
                + ", graphSessionId=" + graphSessionId + ", error=" + error + ", lastErrorMessage=" + lastErrorMessage
                + ", startDelivery=" + startDelivery + ", endDelivery=" + endDelivery
                + ", percentageComplete=" + percentageComplete + ", fileName=" + fileName + ", mimeType=" + mimeType
                + "]";
    }

}