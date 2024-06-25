package it.gov.pagopa.payhub.auth.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDateTime;

@Data
@Document("users")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldNameConstants
public class User {

    @MongoId
    private String userId;
    private String iamIssuer;
    private String mappedExternalUserId;
    private String userCode;
    private LocalDateTime lastLogin;
    private boolean tosAccepted;

}
