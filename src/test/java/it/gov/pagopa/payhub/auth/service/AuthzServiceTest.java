package it.gov.pagopa.payhub.auth.service;

import it.gov.pagopa.payhub.auth.exception.custom.OperatorNotFoundException;
import it.gov.pagopa.payhub.auth.model.Operator;
import it.gov.pagopa.payhub.auth.model.User;
import it.gov.pagopa.payhub.auth.repository.OperatorsRepository;
import it.gov.pagopa.payhub.auth.repository.UsersRepository;
import it.gov.pagopa.payhub.auth.service.user.UserService;
import it.gov.pagopa.payhub.auth.service.user.retrieve.OperatorDTOMapper;
import it.gov.pagopa.payhub.auth.service.user.retrieve.UserDTOMapper;
import it.gov.pagopa.payhub.model.generated.CreateOperatorRequest;
import it.gov.pagopa.payhub.model.generated.OperatorDTO;
import it.gov.pagopa.payhub.model.generated.UserDTO;
import java.util.HashSet;
import java.util.Optional;
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

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private UserDTOMapper userDTOMapper;

    private AuthzService service;

    @BeforeEach
    void init(){
        service = new AuthzServiceImpl(userServiceMock, usersRepository, operatorsRepository, operatorDTOMapper, userDTOMapper);
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
    void whenGetOrganizationOperatorsWithParamsThenCallAutzhService(){
        // Given
        String organizationIpaCode = "IPACODE";
        String fiscalCode = "FISCALCODE";
        String userId = "USERID";
        String firstName = "FIRSTNAME";
        String lastName = "LASTNAME";
        Pageable pageRequest = PageRequest.of(0, 1);

        User user = new User();
        user.setFiscalCode(fiscalCode);
        user.setUserId(userId);

        Operator operator = new Operator();
        operator.setOrganizationIpaCode(organizationIpaCode);
        operator.setUserId(userId);
        operator.setOperatorId(userId+organizationIpaCode);

        OperatorDTO operatorDTO = new OperatorDTO();
        operatorDTO.setOperatorId(operator.getOperatorId());
        operatorDTO.setUserId(user.getUserId());

        Page<User> userPage = new PageImpl<>(List.of(user), pageRequest, 1);

        Mockito.when(usersRepository.retrieveUsers(fiscalCode, firstName, lastName, pageRequest)).thenReturn(userPage);
        Mockito.when(operatorsRepository.findById(userId +organizationIpaCode)).thenReturn(Optional.of(operator));
        Mockito.when(operatorDTOMapper.apply(user, operator)).thenReturn(operatorDTO);

        // When
        Page<OperatorDTO> result = service.getOrganizationOperators(organizationIpaCode, fiscalCode, firstName, lastName, pageRequest);

        // Then
        Assertions.assertEquals(1, result.getTotalElements());
        Assertions.assertEquals(operatorDTO, result.getContent().get(0));
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
    void whenGetOrganizationOperatorThenGetOperatorDTO() {
        //given
        String organizationIpaCode = "IPACODE";
        String mappedExternalUserId = "OPERATORID";

        User user = new User();
        Operator operator = new Operator();
        OperatorDTO expectedOperatorDTO = new OperatorDTO();


        Mockito.when(usersRepository.findByMappedExternalUserId(mappedExternalUserId)).thenReturn(Optional.of(user));
        Mockito.when(operatorsRepository.findById(user.getUserId()+organizationIpaCode)).thenReturn(Optional.of(operator));
        Mockito.when(service.getOrganizationOperator(organizationIpaCode,mappedExternalUserId)).thenReturn(expectedOperatorDTO);

        //when
        OperatorDTO actualOperator = service.getOrganizationOperator(organizationIpaCode, mappedExternalUserId);

        //Then
        Assertions.assertSame(expectedOperatorDTO, actualOperator);
    }

    @Test
    void givenOperatorNotExistedWhenGetOrganizationOperatorThenException() {
        String organizationIpaCode = "IPACODE";
        String mappedExternalUserId = "MAPPEDEXTERNALUSERID";
        User user = new User();

        Mockito.when(usersRepository.findByMappedExternalUserId(mappedExternalUserId)).thenReturn(Optional.of(user));
        Mockito.when(operatorsRepository.findById(user.getUserId()+organizationIpaCode)).thenReturn(Optional.empty());

        OperatorNotFoundException exception = Assertions.assertThrows(OperatorNotFoundException.class, () ->
            service.getOrganizationOperator(organizationIpaCode, mappedExternalUserId));

        Assertions.assertEquals("Operator with this userId "+ user.getUserId()+organizationIpaCode + "not found", exception.getMessage());

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

    @Test
    void whenCreateUserThenVerifyuser() {
        // Given
        User mockUser = new User();
        UserDTO expectedUser = new UserDTO();
        expectedUser.setUserId("USERID");
        expectedUser.setIamIssuer("IAMISSUER");
        expectedUser.setMappedExternalUserId("MAPPEDEXTERNALUSERID");
        expectedUser.setUserCode("USERCODE");
        expectedUser.setFiscalCode("FISCALCODE");
        expectedUser.setFirstName("FIRSTNAME");
        expectedUser.setLastName("LASTNAME");
        expectedUser.setEmail("EMAIL");

        Mockito.when(userServiceMock.registerUser(expectedUser.getMappedExternalUserId(), expectedUser.getFiscalCode(),
            "MYPAY", expectedUser.getFirstName(), expectedUser.getLastName(), expectedUser.getEmail())).thenReturn(mockUser);
        Mockito.when(userDTOMapper.map(mockUser)).thenReturn(expectedUser);

        // When
        UserDTO actualUserDTO = service.createUser(expectedUser);

        // Then
        Assertions.assertEquals(expectedUser, actualUserDTO);
    }



}
