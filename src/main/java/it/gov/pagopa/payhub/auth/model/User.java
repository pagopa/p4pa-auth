package it.gov.pagopa.payhub.auth.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document("users")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldNameConstants
public class User {

    @Id
    private String userId;
    private String iamIssuer;
    private String mappedExternalUserId;
    private String userCode;
    private LocalDateTime lastLogin;
    private boolean tosAccepted;

//region PII temporarily stored
    private String fiscalCode;
    private String firstName;
    private String lastName;
//endregion
}
