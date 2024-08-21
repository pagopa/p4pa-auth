package it.gov.pagopa.payhub.auth.service.user.retrieve;

import it.gov.pagopa.payhub.auth.model.Operator;
import it.gov.pagopa.payhub.auth.model.User;
import it.gov.pagopa.payhub.model.generated.OperatorDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.function.BiFunction;

@Service
public class OperatorDTOMapper implements BiFunction<User, Operator, OperatorDTO> {
    @Override
    public OperatorDTO apply(User user, Operator operator) {
        return OperatorDTO.builder()
                .userId(user.getUserId())
                .mappedExternalUserId(user.getMappedExternalUserId())
                .userCode(user.getUserCode())
                .operatorId(operator.getOperatorId())
                .roles(new ArrayList<>(operator.getRoles()))
                .organizationIpaCode(operator.getOrganizationIpaCode())
                .fiscalCode(user.getFiscalCode())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .build();
    }
}
