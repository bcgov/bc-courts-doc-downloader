package ca.bc.gov.ag.courts.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:application.properties")
public class AppProperties {

	@Value("${application.name}")
	private String appName;

	@Value("${ords.endpoint}")
	private String ordsEndpoint;

	@Value("${ords.auth.password}")
	private String ordsPassword;

	@Value("${ords.auth.username}")
	private String ordsUserName;

	@Value("${ords.app.id}")
	private String appId;

	@Value("${ords.app.pwd}")
	private String appPwd;

	@Value("${ords.app.ticketlifetime}")
	private String ticLifeTime;

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getOrdsEndpoint() {
		return ordsEndpoint;
	}

	public void setOrdsEndpoint(String ordsEndpoint) {
		this.ordsEndpoint = ordsEndpoint;
	}

	public String getOrdsPassword() {
		return ordsPassword;
	}

	public void setOrdsPassword(String ordsPassword) {
		this.ordsPassword = ordsPassword;
	}

	public String getOrdsUserName() {
		return ordsUserName;
	}

	public void setOrdsUserName(String ordsUserName) {
		this.ordsUserName = ordsUserName;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getAppPwd() {
		return appPwd;
	}

	public void setAppPwd(String appPwd) {
		this.appPwd = appPwd;
	}

	public String getTicLifeTime() {
		return ticLifeTime;
	}

	public void setTicLifeTime(String ticLifeTime) {
		this.ticLifeTime = ticLifeTime;
	}

}