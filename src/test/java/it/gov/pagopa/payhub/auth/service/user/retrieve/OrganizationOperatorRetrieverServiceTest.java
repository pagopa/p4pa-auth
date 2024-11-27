package it.gov.pagopa.payhub.auth.service.user.retrieve;

import it.gov.pagopa.payhub.auth.model.Operator;
import it.gov.pagopa.payhub.auth.model.User;
import it.gov.pagopa.payhub.auth.repository.OperatorsRepository;
import it.gov.pagopa.payhub.auth.repository.UsersRepository;
import it.gov.pagopa.payhub.model.generated.OperatorDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class OrganizationOperatorRetrieverServiceTest {

    @Mock
    private OperatorsRepository operatorsRepositoryMock;
    @Mock
    private UsersRepository usersRepositoryMock;
    @Mock
    private OperatorDTOMapper operatorDTOMapperMock;

    private OrganizationOperatorRetrieverService service;

    @BeforeEach
    void init() {
        service = new OrganizationOperatorRetrieverService(operatorsRepositoryMock, usersRepositoryMock, operatorDTOMapperMock);
    }

    @AfterEach
    void verifyNotMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                operatorsRepositoryMock,
                usersRepositoryMock,
                operatorDTOMapperMock);
    }

    @Test
    void givenNoOperatorsWhenRetrieveOrganizationOperatorsThenEmptyList() {
        // Given
        String organizationIpaCode = "IPACODE";
        Pageable pageRequest = PageRequest.of(0,1);

        Mockito.when(operatorsRepositoryMock.findAllByOrganizationIpaCode(organizationIpaCode, pageRequest)).thenReturn(new PageImpl<>(Collections.emptyList()));

        // When
        Page<OperatorDTO> result = service.retrieveOrganizationOperators(organizationIpaCode, pageRequest);

        // Then
        Assertions.assertEquals(new PageImpl<>(Collections.emptyList(), pageRequest, 0), result);
    }

    @Test
    void givenNoUsersWhenRetrieveOrganizationOperatorsThenEmptyList() {
        // Given
        String organizationIpaCode = "IPACODE";
        Pageable pageRequest = PageRequest.of(0,1);

        Mockito.when(operatorsRepositoryMock.findAllByOrganizationIpaCode(organizationIpaCode, pageRequest)).thenReturn(new PageImpl<>(List.of(Operator.builder().userId("USERID").build())));
        Mockito.when(usersRepositoryMock.findById("USERID")).thenReturn(Optional.empty());

        // When
        Page<OperatorDTO> result = service.retrieveOrganizationOperators(organizationIpaCode, pageRequest);

        // Then
        Assertions.assertEquals(new PageImpl<>(Collections.emptyList(), pageRequest, 1), result);
    }

    @Test
    void givenOperatorsAndUsersWhenRetrieveOrganizationOperatorsThenEmptyList() {
        // Given
        String organizationIpaCode = "IPACODE";
        Pageable pageRequest = PageRequest.of(0,1);

        Operator op1 = Operator.builder().userId("USERID1").build();
        Operator op2 = Operator.builder().userId("USERID2").build();
        Operator op3 = Operator.builder().userId("USERID3").build();
        Operator op4 = Operator.builder().userId("USERID4").build();
        Mockito.when(operatorsRepositoryMock.findAllByOrganizationIpaCode(organizationIpaCode, pageRequest)).thenReturn(new PageImpl<>(List.of(
                op1,
                op2,
                op3,
                op4
        )));

        User us1 = User.builder().userId("USERID1").build();
        User us3 = User.builder().userId("USERID3").build();
        Mockito.when(usersRepositoryMock.findById("USERID1")).thenReturn(Optional.of(us1));
        Mockito.when(usersRepositoryMock.findById("USERID2")).thenReturn(Optional.empty());
        Mockito.when(usersRepositoryMock.findById("USERID3")).thenReturn(Optional.of(us3));
        Mockito.when(usersRepositoryMock.findById("USERID4")).thenReturn(Optional.empty());

        OperatorDTO expectedOpDto1 = new OperatorDTO();
        OperatorDTO expectedOpDto3 = new OperatorDTO();
        Mockito.when(operatorDTOMapperMock.apply(us1, op1)).thenReturn(expectedOpDto1);
        Mockito.when(operatorDTOMapperMock.apply(us3, op3)).thenReturn(expectedOpDto1);

        // When
        Page<OperatorDTO> result = service.retrieveOrganizationOperators(organizationIpaCode, pageRequest);

        // Then
        Assertions.assertEquals(
                new PageImpl<>(List.of(expectedOpDto1, expectedOpDto3), pageRequest, 4),
                result);
    }
}
