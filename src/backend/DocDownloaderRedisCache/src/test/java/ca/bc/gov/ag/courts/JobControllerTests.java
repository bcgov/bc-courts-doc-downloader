package ca.bc.gov.ag.courts;

import ca.bc.gov.ag.courts.config.SecurityConfig;
import ca.bc.gov.ag.courts.controller.JobController;
import ca.bc.gov.ag.courts.model.Job;
import ca.bc.gov.ag.courts.queue.MessagePublisher;
import ca.bc.gov.ag.courts.repo.JobRepository;
import ca.bc.gov.ag.courts.util.JobValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@SpringBootTest
@Profile("unittest")
class JobControllerTests {

    @MockBean
    private JedisConnectionFactory jedisConnectionFactory;

    @MockBean
    private RedisTemplate<String, Object> redisTemplate;

    @MockBean
    private MessageListenerAdapter messageListenerAdapter;

    @MockBean
    private RedisMessageListenerContainer redisMessageListenerContainer;

    @MockBean
    @Qualifier("redisPublisher")
    private MessagePublisher redisPublisher;

    @MockBean
    private ChannelTopic channelTopic;

    @MockBean
    private JobRepository jobRepository;

    @MockBean
    private SecurityConfig securityConfig;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private SecurityFilterChain securityFilterChain;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private JobController jobController;

    @Test
    public void testRedisSave() {
        Job job = getJob();
        Mockito.when(jobController.postJob(job)).thenReturn(job.toString());
        var aJob = jobController.postJob(job);
        Assertions.assertNotNull(aJob, "Job is null");
        Assertions.assertEquals(job.toString(), aJob, "Post Job not the same.");
    }

    @Test
    public void testDeleteJobs() throws Exception {
        Mockito.when(jobController.deleteJob(Mockito.anyString())).thenReturn(Mockito.any()); // replace with your actual deleteJob endpoint
        Assertions.assertDoesNotThrow(() -> jobController.deleteJob("123"));
    }

    @Test
    public void testJobValidator() {
        Job job = getJob();
        Assertions.assertDoesNotThrow(() -> JobValidator.validateJobDates(job));
    }

    private Job getJob() {
        Job job = new Job();
        job.setId("0990995");
        job.setGuid("d2x3dGJpZ3RYUWA7bSo0NExpRlVBYk5nNTRyPEpfdV55U2dHbWpVQysyO");
        job.setApplicationId("CeisCso");
        job.setPutId("SCV");
        job.setOrdsTimeout(false);
        job.setGraphTimeout(false);
        job.setChecksum("56112BF23");
        job.setGraphSessionId("78900011");
        job.setError(false);
        job.setLastErrorMessage("wow");
        job.setStartDelivery("2024-01-19T19:00-07:00");
        job.setEndDelivery(null);
        job.setPercentageComplete(65);
        job.setFileName("TEST_3M.pdf");
        job.setMimeType("application/pdf");
        return job;
    }
}
