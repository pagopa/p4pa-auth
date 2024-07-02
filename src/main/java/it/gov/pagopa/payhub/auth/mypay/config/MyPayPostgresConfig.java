package it.gov.pagopa.payhub.auth.mypay.config;

import it.gov.pagopa.payhub.auth.utils.Constants;
import jakarta.persistence.EntityManagerFactory;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    entityManagerFactoryRef = "myPayEntityManagerFactory",
    transactionManagerRef = "myPayTransactionManager",
    basePackages = {"it.gov.pagopa.payhub.auth.mypay"}
)
public class MyPayPostgresConfig {

  private static final String PERSISTANCE_NAME = "mypay";

  @Primary
  @Bean(name = "myPayDataSource")
  @ConfigurationProperties("spring.data.mypay")
  public DataSource myPayDataSource() {
    return DataSourceBuilder.create().build();
  }

  @Primary
  @Bean(name = "myPayEntityManagerFactory")
  public LocalContainerEntityManagerFactoryBean myPayEntityManagerFactory(
      @Qualifier("myPayDataSource") DataSource dataSource,
      EntityManagerFactoryBuilder builder) {

      return builder.dataSource(dataSource)
          .packages("it.gov.pagopa.payhub.auth.mypay")
          .properties(Constants.jpaProperties())
          .persistenceUnit(PERSISTANCE_NAME)
          .build();
  }


  @Primary
  @Bean(name = "myPayTransactionManager")
  public PlatformTransactionManager myPayTransactionManager(
      @Qualifier("myPayEntityManagerFactory")EntityManagerFactory myPayEntityManagerFactory) {

    return new JpaTransactionManager(myPayEntityManagerFactory);
  }

}
