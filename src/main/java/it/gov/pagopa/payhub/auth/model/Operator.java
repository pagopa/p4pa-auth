package it.gov.pagopa.payhub.auth.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.Set;

@Data
@Document
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldNameConstants
public class Operator {

    @MongoId
    private String operatorId;
    private String userId;
    private Set<String> roles;
    private String organizationIpaCode;

}
