package it.gov.pagopa.payhub.auth.model;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDateTime;

@Data
@Document
public class Users {

    @MongoId
    private String userId;
    private String iamIssuer;
    private String mappedExternalUserId;
    private String userCode;
    private LocalDateTime lastLogin;
    private boolean tosAccepted;

}
