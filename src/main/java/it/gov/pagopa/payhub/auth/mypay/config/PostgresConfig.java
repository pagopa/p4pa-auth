package it.gov.pagopa.payhub.auth.mypay.config;

import javax.sql.DataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = {"it.gov.pagopa.payhub.auth.mypay"})
public class PostgresConfig {

  @Bean(name = "myPayDataSource")
  @ConfigurationProperties(prefix = "spring.data.mypay")
  public DataSource myPayDataSource() {
    return DataSourceBuilder.create().build();
  }
}
