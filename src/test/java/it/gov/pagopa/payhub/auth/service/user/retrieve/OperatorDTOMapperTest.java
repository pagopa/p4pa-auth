package it.gov.pagopa.payhub.auth.service.user.retrieve;

import it.gov.pagopa.payhub.auth.model.Operator;
import it.gov.pagopa.payhub.auth.model.User;
import it.gov.pagopa.payhub.model.generated.OperatorDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

class OperatorDTOMapperTest {

    private final OperatorDTOMapper mapper = new OperatorDTOMapper();

    @Test
    void test(){
        // Given
        User user = User.builder()
                .userId("USERID")
                .iamIssuer("IAMISSUER")
                .mappedExternalUserId("MAPPEDEXTERNALUSERID")
                .userCode("USERCODE")
                .fiscalCode("FISCALCODE")
                .firstName("FIRSTNAME")
                .lastName("LASTNAME")
                .email("EMAIL")
                .build();

        Operator operator = Operator.builder()
                .operatorId("OPERATORID")
                .userId("USERID")
                .roles(Set.of("ROLES"))
                .organizationIpaCode("ORGANIZATIONIPACODE")
                .build();

        // When
        OperatorDTO result = mapper.apply(user, operator);

        // Then
        Assertions.assertEquals(
                OperatorDTO.builder()
                        .userId("USERID")
                        .mappedExternalUserId("MAPPEDEXTERNALUSERID")
                        .userCode("USERCODE")
                        .operatorId("OPERATORID")
                        .roles(List.of("ROLES"))
                        .organizationIpaCode("ORGANIZATIONIPACODE")
                        .fiscalCode("FISCALCODE")
                        .firstName("FIRSTNAME")
                        .lastName("LASTNAME")
                        .email("EMAIL")
                        .build(),
                result
        );
    }
}
