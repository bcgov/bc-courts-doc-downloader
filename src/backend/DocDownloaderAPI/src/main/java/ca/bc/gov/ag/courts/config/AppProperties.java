package ca.bc.gov.ag.courts.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties()
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
	
	@Value("${msg.clientId}")
	private String msgClientId;
	
	@Value("${msg.authority}")
	private String msgAuthority;
	
	@Value("${msg.secretKey}")
	private String msgSecretKey;
	
	@Value("${msg.endpointHost}")
	private String msgEndpointHost;
	
	@Value("${redis.client.host}")
	private String redisClientHost;
	
	@Value("${redis.client.port}")
	private String redisClientPort;
	
	@Value("${redis.client.username}")
	private String redisClientUsername;
	
	@Value("${redis.client.password}")
	private String redisClientPassword;
	
	@Value("${s3.access.endpoint}")
	private String s3AccessEndpoint;
	
	@Value("${s3.access.bucket}")
	private String s3AccessBucket;
	
	@Value("${s3.access.keyid}")
	private String s3AccessKeyid;
	
	@Value("${s3.access.secretkey}")
	private String s3AccessSecretkey;

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

	public String getMsgClientId() {
		return msgClientId;
	}

	public void setMsgClientId(String msgClientId) {
		this.msgClientId = msgClientId;
	}

	public String getMsgAuthority() {
		return msgAuthority;
	}

	public void setMsgAuthority(String msgAuthority) {
		this.msgAuthority = msgAuthority;
	}

	public String getMsgSecretKey() {
		return msgSecretKey;
	}

	public void setMsgSecretKey(String msgSecretKey) {
		this.msgSecretKey = msgSecretKey;
	}

	public String getMsgEndpointHost() {
		return msgEndpointHost;
	}

	public void setMsgEndpointHost(String msgEndpointHost) {
		this.msgEndpointHost = msgEndpointHost;
	}

	public String getRedisClientHost() {
		return redisClientHost;
	}

	public void setRedisClientHost(String redisClientHost) {
		this.redisClientHost = redisClientHost;
	}

	public String getRedisClientPort() {
		return redisClientPort;
	}

	public void setRedisClientPort(String redisClientPort) {
		this.redisClientPort = redisClientPort;
	}

	public String getRedisClientUsername() {
		return redisClientUsername;
	}

	public void setRedisClientUsername(String redisClientUsername) {
		this.redisClientUsername = redisClientUsername;
	}

	public String getRedisClientPassword() {
		return redisClientPassword;
	}

	public void setRedisClientPassword(String redisClientPassword) {
		this.redisClientPassword = redisClientPassword;
	}

	public String getS3AccessEndpoint() {
		return s3AccessEndpoint;
	}

	public void setS3AccessEndpoint(String s3AccessEndpoint) {
		this.s3AccessEndpoint = s3AccessEndpoint;
	}

	public String getS3AccessBucket() {
		return s3AccessBucket;
	}

	public void setS3AccessBucket(String s3AccessBucket) {
		this.s3AccessBucket = s3AccessBucket;
	}

	public String getS3AccessKeyid() {
		return s3AccessKeyid;
	}

	public void setS3AccessKeyid(String s3AccessKeyid) {
		this.s3AccessKeyid = s3AccessKeyid;
	}

	public String getS3AccessSecretkey() {
		return s3AccessSecretkey;
	}

	public void setS3AccessSecretkey(String s3AccessSecretkey) {
		this.s3AccessSecretkey = s3AccessSecretkey;
	}

}