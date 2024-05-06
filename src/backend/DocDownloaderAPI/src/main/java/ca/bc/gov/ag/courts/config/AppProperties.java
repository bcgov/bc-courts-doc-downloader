package ca.bc.gov.ag.courts.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:application.properties")
public class AppProperties {
	
	@Value("${application.password}")
	private String applicationPassword;
	
	@Value("${application.username}")
	private String applicationUsername;

	@Value("${application.name}")
	private String applicationName;
	
	@Value("${application.version}")
	private String applicationVersion;
	
	@Value("${application.net.max.retries}")
	private int maxRetries;
	
	@Value("${application.net.delay}")
	private String retryDelay;

	@Value("${ords.ssg.base.url}")
	private String ordsSsgBaseUrl;

	@Value("${ords.ssg.password}")
	private String ordsSsgPassword;
	
	@Value("${ords.ssg.username}")
	private String ordsSsgUsername;

	@Value("${ords.put.id}")
	private String ordsPutId;
	
	@Value("${ords.application.id}")
	private String ordsApplicationId;
	
	@Value("${ords.username}")
	private String ordsUsername;

	@Value("${ords.password}")
	private String ordsPassword;
	
	@Value("${ords.database.id}")
	private String ordsDatabaseId;
	
	@Value("${ords.server}")
	private String ordsServer;

	@Value("${ords.ticketlifetime}")
	private String ordsTicketLifetime;

	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public int getMaxRetries() {
		return maxRetries;
	}

	public void setMaxRetries(int maxRetries) {
		this.maxRetries = maxRetries;
	}

	public String getRetryDelay() {
		return retryDelay;
	}

	public void setRetryDelay(String retryDelay) {
		this.retryDelay = retryDelay;
	}

	public String getOrdsSsgBaseUrl() {
		return ordsSsgBaseUrl;
	}

	public void setOrdsSsgBaseUrl(String ordsSsgBaseUrl) {
		this.ordsSsgBaseUrl = ordsSsgBaseUrl;
	}

	public String getOrdsSsgPassword() {
		return ordsSsgPassword;
	}

	public void setOrdsSsgPassword(String ordsSsgPassword) {
		this.ordsSsgPassword = ordsSsgPassword;
	}

	public String getOrdsSsgUsername() {
		return ordsSsgUsername;
	}

	public void setOrdsSsgUsername(String ordsSsgUsername) {
		this.ordsSsgUsername = ordsSsgUsername;
	}

	public String getOrdsApplicationId() {
		return ordsApplicationId;
	}

	public void setOrdsApplicationId(String ordsApplicationId) {
		this.ordsApplicationId = ordsApplicationId;
	}

	public String getOrdsUsername() {
		return ordsUsername;
	}

	public void setOrdsUsername(String ordsUsername) {
		this.ordsUsername = ordsUsername;
	}

	public String getOrdsPassword() {
		return ordsPassword;
	}

	public void setOrdsPassword(String ordsPassword) {
		this.ordsPassword = ordsPassword;
	}

	public String getOrdsTicketLifetime() {
		return ordsTicketLifetime;
	}

	public void setOrdsTicketLifetime(String ordsTicketLifetime) {
		this.ordsTicketLifetime = ordsTicketLifetime;
	}

	public String getOrdsPutId() {
		return ordsPutId;
	}

	public void setOrdsPutId(String ordsPutId) {
		this.ordsPutId = ordsPutId;
	}

	public String getOrdsDatabaseId() {
		return ordsDatabaseId;
	}

	public void setOrdsDatabaseId(String ordsDatabaseId) {
		this.ordsDatabaseId = ordsDatabaseId;
	}

	public String getOrdsServer() {
		return ordsServer;
	}

	public void setOrdsServer(String ordsServer) {
		this.ordsServer = ordsServer;
	}

	public String getApplicationPassword() {
		return applicationPassword;
	}

	public void setApplicationPassword(String applicationPassword) {
		this.applicationPassword = applicationPassword;
	}

	public String getApplicationUsername() {
		return applicationUsername;
	}

	public void setApplicationUsername(String applicationUsername) {
		this.applicationUsername = applicationUsername;
	}

	public String getApplicationVersion() {
		return applicationVersion;
	}

	public void setApplicationVersion(String applicationVersion) {
		this.applicationVersion = applicationVersion;
	}

}