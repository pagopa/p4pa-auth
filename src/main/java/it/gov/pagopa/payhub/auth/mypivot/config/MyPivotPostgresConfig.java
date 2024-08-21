package it.gov.pagopa.payhub.auth.mypivot.config;

import it.gov.pagopa.payhub.auth.utils.Constants;
import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    entityManagerFactoryRef = "myPivotEntityManagerFactory",
    transactionManagerRef = "myPivotTransactionManager",
    basePackages = {"it.gov.pagopa.payhub.auth.mypivot"}
)
public class MyPivotPostgresConfig {

  private static final String PERSISTANCE_NAME = "mypivot";

  @Bean(name = "myPivotDataSource")
  @ConfigurationProperties("spring.data.mypivot")
  public DataSource myPivotDataSource() {
    return DataSourceBuilder.create().build();
  }

  @Bean(name = "myPivotEntityManagerFactory")
  public LocalContainerEntityManagerFactoryBean myPivotEntityManagerFactory(
      @Qualifier("myPivotDataSource") DataSource dataSource,
      EntityManagerFactoryBuilder builder) {

    return builder.dataSource(dataSource)
        .packages("it.gov.pagopa.payhub.auth.mypivot")
        .persistenceUnit(PERSISTANCE_NAME)
        .properties(Constants.jpaProperties())
        .build();
  }

  @Bean(name = "myPivotTransactionManager")
  public PlatformTransactionManager myPivotTransactionManager(
      @Qualifier("myPivotEntityManagerFactory")EntityManagerFactory myPivotEntityManagerFactory) {

    return new JpaTransactionManager(myPivotEntityManagerFactory);
  }



}
