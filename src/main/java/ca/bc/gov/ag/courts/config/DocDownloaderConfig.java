package ca.bc.gov.ag.courts.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;


@Configuration
@ComponentScan("ca.bc.gov.ag.courts")
@EnableRetry
@EnableAsync
public class DocDownloaderConfig  {
}
