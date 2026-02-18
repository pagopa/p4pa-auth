package it.gov.pagopa.payhub.auth.model;

import lombok.*;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("clients")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldNameConstants
@EqualsAndHashCode(callSuper = false)
public class Client extends BaseEntity {

	@Id
	private String clientId;
	private String clientName;
	private String organizationIpaCode;
	private byte[] clientSecret;
}
