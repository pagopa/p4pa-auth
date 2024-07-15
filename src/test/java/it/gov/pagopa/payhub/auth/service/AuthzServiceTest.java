package it.gov.pagopa.payhub.auth.service;

import it.gov.pagopa.payhub.auth.mypay.repository.MyPayOperatorsRepository;
import it.gov.pagopa.payhub.auth.mypivot.repository.MyPivotOperatorsRepository;
import it.gov.pagopa.payhub.auth.repository.OperatorsRepository;
import it.gov.pagopa.payhub.auth.service.user.UserService;
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

import java.util.List;

@ExtendWith(MockitoExtension.class)
class AuthzServiceTest {

    @Mock
    private UserService userServiceMock;

    @Mock
    private OperatorsRepository operatorsRepository;

    @Mock
    private MyPayOperatorsRepository myPayOperatorsRepository;

    @Mock
    private MyPivotOperatorsRepository myPivotOperatorsRepository;

    private AuthzService service;

    @BeforeEach
    void init(){
        service = new AuthzServiceImpl(userServiceMock, operatorsRepository, myPayOperatorsRepository, myPivotOperatorsRepository);
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
    void whenDeleteOrganizationOperationThenVerifyDelete() {
        String organizationIpaCode = "IPACODE";
        String operatorId = "OPERATORID";

        //When
        service.deleteOrganizationOperator(organizationIpaCode, operatorId);
        //Then
        Mockito.verify(operatorsRepository).deleteByOperatorIdAndOrganizationIpaCode(operatorId,organizationIpaCode);
        Mockito.verify(myPayOperatorsRepository).deleteOrganizationOperator(operatorId, organizationIpaCode);
        Mockito.verify(myPivotOperatorsRepository).deleteOrganizationOperator(operatorId, organizationIpaCode);
    }

}
