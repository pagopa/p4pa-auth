package it.gov.pagopa.payhub.auth.model;

import lombok.*;
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
@EqualsAndHashCode(callSuper = false)
public class Operator extends BaseEntity {

    @Id
    private String operatorId;
    private String userId;
    @Builder.Default
    private Set<String> roles = new HashSet<>();
    private String organizationIpaCode;
    private String email;

}
