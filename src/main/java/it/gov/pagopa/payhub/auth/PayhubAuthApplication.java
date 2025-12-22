package it.gov.pagopa.payhub.auth;

import it.gov.pagopa.payhub.auth.utils.Constants;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.webmvc.autoconfigure.error.ErrorMvcAutoConfiguration;

import java.util.TimeZone;

@SpringBootApplication(exclude = {UserDetailsServiceAutoConfiguration.class, ErrorMvcAutoConfiguration.class})
public class PayhubAuthApplication {

	public static void main(String[] args) {
        TimeZone.setDefault(Constants.DEFAULT_TIMEZONE);
        SpringApplication.run(PayhubAuthApplication.class, args);
	}

}
