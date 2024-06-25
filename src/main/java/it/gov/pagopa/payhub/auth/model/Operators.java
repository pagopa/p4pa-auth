package it.gov.pagopa.payhub.auth.model;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.Set;

@Data
@Document
public class Operators {

    @MongoId
    private String operatorId;
    private String userId;
    private Set<String> roles;
    private String organizationIpaCode;

}
