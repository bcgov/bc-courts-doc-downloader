package ca.bc.gov.ag.courts.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "AppId", "AppPwd", "TicketLifeTime" })
public class InitializeRequest {

	@JsonProperty("AppId")
	private String appId;
	@JsonProperty("AppPwd")
	private String appPwd;
	@JsonProperty("TicketLifeTime")
	private String ticketLifeTime;

	public InitializeRequest(String appId, String appPwd, String ticLifeTime) {
		this.appId = appId;
		this.appPwd = appPwd;
		this.ticketLifeTime = ticLifeTime;
	}

	@JsonProperty("AppId")
	public String getAppId() {
		return appId;
	}

	@JsonProperty("AppId")
	public void setAppId(String appId) {
		this.appId = appId;
	}

	@JsonProperty("AppPwd")
	public String getAppPwd() {
		return appPwd;
	}

	@JsonProperty("AppPwd")
	public void setAppPwd(String appPwd) {
		this.appPwd = appPwd;
	}

	@JsonProperty("TicketLifeTime")
	public String getTicketLifeTime() {
		return ticketLifeTime;
	}

	@JsonProperty("TicketLifeTime")
	public void setTicketLifeTime(String ticketLifeTime) {
		this.ticketLifeTime = ticketLifeTime;
	}

}
