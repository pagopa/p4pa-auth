package it.gov.pagopa.payhub.auth.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;

@Data
@Document("operators")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldNameConstants
public class Operator {

    @Id
    private String operatorId;
    private String userId;
    @Builder.Default
    private Set<String> roles = new HashSet<>();
    private String organizationIpaCode;

}
