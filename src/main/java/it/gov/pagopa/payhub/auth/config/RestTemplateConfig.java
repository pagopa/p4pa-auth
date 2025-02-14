package it.gov.pagopa.payhub.auth.config;

import it.gov.pagopa.payhub.auth.performancelogger.RestInvokePerformanceLogger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.client.RestTemplateBuilderConfigurer;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration(proxyBeanMethods = false)
public class RestTemplateConfig {
  private final int connectTimeoutMillis;
  private final int readTimeoutHandlerMillis;

  public RestTemplateConfig(
      @Value("${rest.default-timeout.connect-millis}") int connectTimeoutMillis,
      @Value("${rest.default-timeout.read-millis}") int readTimeoutHandlerMillis) {
    this.connectTimeoutMillis = connectTimeoutMillis;
    this.readTimeoutHandlerMillis = readTimeoutHandlerMillis;
  }

  @Bean
  public RestTemplateBuilder restTemplateBuilder(RestTemplateBuilderConfigurer configurer) {
      return configurer.configure(new RestTemplateBuilder())
          .additionalInterceptors(new RestInvokePerformanceLogger())
          .connectTimeout(Duration.ofMillis(connectTimeoutMillis))
          .readTimeout(Duration.ofMillis(readTimeoutHandlerMillis));
  }
}
