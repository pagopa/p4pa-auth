package it.gov.pagopa.payhub.auth.mypay.config;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class JdbcTemplateConfig {

  @Bean(name = "myPayJdbcTemplate")
  public JdbcTemplate myPayJdbcTemplate(@Qualifier("myPayDataSource") DataSource dataSource) {
    return new JdbcTemplate(dataSource);
  }
}
