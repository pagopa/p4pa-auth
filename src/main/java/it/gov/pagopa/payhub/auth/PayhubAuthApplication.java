package it.gov.pagopa.payhub.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "it.gov.pagopa.payhub")
public class PayhubAuthApplication {

	public static void main(String[] args) {
		SpringApplication.run(PayhubAuthApplication.class, args);
	}

}
