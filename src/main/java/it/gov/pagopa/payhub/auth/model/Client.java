package it.gov.pagopa.payhub.auth.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("clients")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldNameConstants
public class Client {

	@Id
	private String clientId;
	private String organizationIpaCode;
	private String clientSecret;
}
