package it.gov.pagopa.payhub.auth.service;

import it.gov.pagopa.payhub.auth.model.Operator;
import it.gov.pagopa.payhub.auth.model.User;
import it.gov.pagopa.payhub.auth.repository.OperatorsRepository;
import it.gov.pagopa.payhub.auth.service.user.UserService;
import it.gov.pagopa.payhub.auth.service.user.retrieve.OperatorDTOMapper;
import it.gov.pagopa.payhub.model.generated.CreateOperatorRequest;
import it.gov.pagopa.payhub.model.generated.OperatorDTO;
import java.util.Arrays;
import java.util.HashSet;
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

import java.util.List;

@ExtendWith(MockitoExtension.class)
class AuthzServiceTest {

    @Mock
    private UserService userServiceMock;

    @Mock
    private OperatorsRepository operatorsRepository;

    @Mock
    private OperatorDTOMapper operatorDTOMapper;

    private AuthzService service;

    @BeforeEach
    void init(){
        service = new AuthzServiceImpl(userServiceMock, operatorsRepository, operatorDTOMapper);
    }

    @AfterEach
    void verifyNotMoreInteractions(){
        Mockito.verifyNoMoreInteractions(
                userServiceMock
        );
    }

    @Test
    void whengetOrganizationOperatorsThenCallUserService(){
        // Given
        String organizationIpaCode = "IPACODE";
        Pageable pageRequest = PageRequest.of(0, 1);

        PageImpl<OperatorDTO> expectedResult = new PageImpl<>(List.of());
        Mockito.when(userServiceMock.retrieveOrganizationOperators(organizationIpaCode, pageRequest))
                .thenReturn(expectedResult);

        // When
        Page<OperatorDTO> result = service.getOrganizationOperators(organizationIpaCode, pageRequest);

        // Then
        Assertions.assertSame(expectedResult, result);
    }

    @Test
    void whenDeleteOrganizationOperatorThenVerifyDelete() {
        String organizationIpaCode = "IPACODE";
        String mappedExternalUserId = "OPERATORID";

        //When
        service.deleteOrganizationOperator(organizationIpaCode, mappedExternalUserId);
        //Then
        Mockito.verify(operatorsRepository).deleteOrganizationOperator(organizationIpaCode,mappedExternalUserId);
    }

    @Test
    void whenCreateOrganizationOperatorThenVerifyOperator() {
        String organizationIpaCode = "organizationIpaCode";
        CreateOperatorRequest createOperatorRequest = new CreateOperatorRequest();
        createOperatorRequest.setExternalUserId("externalUserId");
        createOperatorRequest.setFiscalCode("fiscalCode");
        createOperatorRequest.setFirstName("firstName");
        createOperatorRequest.setLastName("lastName");
        createOperatorRequest.setEmail("email@example.com");
        createOperatorRequest.setRoles(List.of("ROLE_ADMIN"));

        User mockUser = new User();
        Operator mockOperator = new Operator();
        OperatorDTO expectedOperatorDTO = new OperatorDTO();

        Mockito.when(userServiceMock.registerUser(createOperatorRequest.getExternalUserId(), createOperatorRequest.getFiscalCode(),
            "MYPAY", createOperatorRequest.getFirstName(), createOperatorRequest.getLastName(), createOperatorRequest.getEmail())).thenReturn(mockUser);
        Mockito.when(userServiceMock.registerOperator(mockUser.getUserId(), organizationIpaCode, new HashSet<>(createOperatorRequest.getRoles()),
                    createOperatorRequest.getExternalUserId(), mockUser.getEmail())).thenReturn(mockOperator);
        Mockito.when(operatorDTOMapper.apply(mockUser, mockOperator)).thenReturn(expectedOperatorDTO);

        OperatorDTO actualOperatorDTO = service.createOrganizationOperator(organizationIpaCode, createOperatorRequest);

        Assertions.assertEquals(expectedOperatorDTO, actualOperatorDTO);
    }



}
