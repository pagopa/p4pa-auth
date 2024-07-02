package it.gov.pagopa.payhub.auth.utils;

import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy;
import org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy;

public class Constants {
    private Constants(){}

    public static final ZoneId ZONEID = ZoneId.of("Europe/Rome");

    // Multiple datasource introduce an issue that we need to explicity CamelCaseToUnderscore properties
    // to each EntityManagerFactory
    public static Map<String, Object> jpaProperties() {
        Map<String, Object> props = new HashMap<>();
        props.put("hibernate.physical_naming_strategy", CamelCaseToUnderscoresNamingStrategy.class.getName());
        props.put("hibernate.implicit_naming_strategy", SpringImplicitNamingStrategy.class.getName());
        return props;
    }
}
