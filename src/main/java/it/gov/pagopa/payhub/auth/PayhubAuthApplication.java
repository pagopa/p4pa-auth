package it.gov.pagopa.payhub.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(exclude = {UserDetailsServiceAutoConfiguration.class})
public class PayhubAuthApplication {

	public static void main(String[] args) {
		SpringApplication.run(PayhubAuthApplication.class, args);
	}

}
