package it.gov.pagopa.payhub.auth.service;

import it.gov.pagopa.payhub.auth.exception.custom.OperatorNotFoundException;
import it.gov.pagopa.payhub.auth.model.Operator;
import it.gov.pagopa.payhub.auth.model.User;
import it.gov.pagopa.payhub.auth.repository.ClientRepository;
import it.gov.pagopa.payhub.auth.repository.OperatorsRepository;
import it.gov.pagopa.payhub.auth.repository.UsersRepository;
import it.gov.pagopa.payhub.auth.service.m2m.ClientService;
import it.gov.pagopa.payhub.auth.service.user.UserService;
import it.gov.pagopa.payhub.auth.service.user.registration.ExternalUserIdObfuscatorService;
import it.gov.pagopa.payhub.auth.service.user.retrieve.OperatorDTOMapper;
import it.gov.pagopa.payhub.auth.service.user.retrieve.UserDTOMapper;
import it.gov.pagopa.payhub.dto.generated.*;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class AuthzServiceTest {

    @Mock
    private UserService userServiceMock;

    @Mock
    private ClientService clientServiceMock;

    @Mock
    private ClientRepository clientRepositoryMock;

    @Mock
    private ExternalUserIdObfuscatorService externalUserIdObfuscatorService;

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
        service = new AuthzServiceImpl(
                userServiceMock,
                clientServiceMock,
                clientRepositoryMock,
                externalUserIdObfuscatorService,
                usersRepository,
                operatorsRepository,
                operatorDTOMapper,
                userDTOMapper
        );
    }

    @AfterEach
    void verifyNotMoreInteractions(){
        Mockito.verifyNoMoreInteractions(
                userServiceMock,
                clientServiceMock
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
        assertEquals(1, result.getTotalElements());
        assertEquals(operatorDTO, result.getContent().getFirst());
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
    void whenDeleteOrganizationOperatorByExternalUserIdThenVerifyDelete() {
        String organizationIpaCode = "IPACODE";
        String externalUserId = "externalUserId";

        String mappedExternalUserId = "mappedExternalUserId";

        Mockito.when(externalUserIdObfuscatorService.obfuscate(externalUserId))
                .thenReturn(mappedExternalUserId);

        //When
        service.deleteOrganizationOperatorByExternalUserId(organizationIpaCode, externalUserId);

        //Then
        Mockito.verify(externalUserIdObfuscatorService).obfuscate(externalUserId);
        Mockito.verify(operatorsRepository).deleteOrganizationOperator(organizationIpaCode, mappedExternalUserId);
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

        assertEquals("OPERATOR_NOT_FOUND",exception.getCode());
        assertEquals("Operator with this userId "+ user.getUserId()+organizationIpaCode + "not found", exception.getMessage());

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
            "PU", createOperatorRequest.getFirstName(), createOperatorRequest.getLastName())).thenReturn(mockUser);
        Mockito.when(userServiceMock.registerOperator(mockUser.getUserId(), organizationIpaCode, new HashSet<>(createOperatorRequest.getRoles())
                    , createOperatorRequest.getEmail())).thenReturn(mockOperator);
        Mockito.when(operatorDTOMapper.apply(mockUser, mockOperator)).thenReturn(expectedOperatorDTO);

        OperatorDTO actualOperatorDTO = service.createOrganizationOperator(organizationIpaCode, createOperatorRequest);

        assertEquals(expectedOperatorDTO, actualOperatorDTO);
    }

    @Test
    void whenCreateUserThenVerifyuser() {
        // Given
        User mockUser = new User();
        UserDTO expectedUser = new UserDTO();
        expectedUser.setExternalUserId("ERNALUSERID");
        expectedUser.setFiscalCode("FISCALCODE");
        expectedUser.setFirstName("FIRSTNAME");
        expectedUser.setLastName("LASTNAME");

        Mockito.when(userServiceMock.registerUser(expectedUser.getExternalUserId(), expectedUser.getFiscalCode(),
            "MYPAY", expectedUser.getFirstName(), expectedUser.getLastName())).thenReturn(mockUser);
        Mockito.when(userDTOMapper.map(mockUser)).thenReturn(expectedUser);

        // When
        UserDTO actualUserDTO = service.createUser(expectedUser);

        // Then
        assertEquals(expectedUser, actualUserDTO);
    }

    @Test
    void whenCreateClientThenVerifyClient() {
        //Given
        String organizationIpaCode = "organizationIpaCode";
        CreateClientRequest createClientRequest = new CreateClientRequest();
        createClientRequest.setClientName("clientname");
        ClientDTO expectedClientDTO = new ClientDTO();

        Mockito.when(clientServiceMock.registerClient(createClientRequest.getClientName(), organizationIpaCode)).thenReturn(expectedClientDTO);

        //When
        ClientDTO actualClientDTO = service.registerClient(organizationIpaCode, createClientRequest);
        //Then
        assertEquals(expectedClientDTO, actualClientDTO);
    }

    @Test
    void whenGetUserInfoFromMappedExternalUserIdThenGetUserInfo() {
        //Given
        String mappedExternalUserId = "MAPPEDEXTERNALUSERID";
        String accessToken = "ACCESSTOKEN";
        UserInfo expectedUserInfo = new UserInfo();

        Mockito.when(userServiceMock.getUserInfoFromMappedExternalUserId(mappedExternalUserId, accessToken))
          .thenReturn(expectedUserInfo);

        //When
        UserInfo result = service.getUserInfoFromMappedExternalUserId(mappedExternalUserId, accessToken);

        //Then
        assertEquals(expectedUserInfo, result);
    }

    @Test
    void givenClientIdWhenGetClientThenInvokeClientService() {
        String organizationIpaCode = "organizationIpaCode";
        String clientId = "clientId";

        ClientDTO expectedClientDTO = ClientDTO.builder()
                .clientId(clientId)
                .clientName("Test Client")
                .organizationIpaCode(organizationIpaCode)
                .clientSecret("decryptedSecret")
                .build();

        Mockito.when(clientServiceMock.getClient(organizationIpaCode, clientId))
                .thenReturn(Optional.of(expectedClientDTO));

        Optional<ClientDTO> result = service.getClient(organizationIpaCode, clientId);

        assertTrue(result.isPresent());
        assertEquals(expectedClientDTO, result.get());
    }

    @Test
    void givenOrganizationIpaCodeWhenGetClientsThenInvokeClientService() {
        //Given
        String organizationIpaCode = "organizationIpaCode";
        List<ClientNoSecretDTO> expectedDTOList = new ArrayList<>();

        Mockito.when(clientServiceMock.getClients(organizationIpaCode)).thenReturn(expectedDTOList);

        //When
        List<ClientNoSecretDTO> result = service.getClients(organizationIpaCode);
        //Then
        assertEquals(expectedDTOList, result);
    }

    @Test
    void givenClientIdWhenRevokeClientThenVerifyRevoke() {
        //Given
        String organizationIpaCode = "organizationIpaCode";
        String clientId = "clientId";
        //When
        service.revokeClient(organizationIpaCode, clientId);
        //Then
        Mockito.verify(clientServiceMock).revokeClient(organizationIpaCode, clientId);
    }

    @Test
    void givenClientNameWhenGetClientsSearchThenCallSearchByFiltersWithClientName(){
        String clientId = "CLIENTID";
        String clientName = "CLIENTNAME";
        String organizationIpaCode = "IPACODE";

        Pageable pageRequest = PageRequest.of(0, 1);

        ClientNoSecretDTO clientNoSecretDTO = new ClientNoSecretDTO();
        clientNoSecretDTO.setClientId(clientId);
        clientNoSecretDTO.setClientName(clientName);
        clientNoSecretDTO.setOrganizationIpaCode(organizationIpaCode);

        Page<ClientNoSecretDTO> clientPage = new PageImpl<>(List.of(clientNoSecretDTO), pageRequest, 1);

        Mockito.when(clientRepositoryMock.searchByFiltersWithClientName(clientId,clientName,organizationIpaCode,pageRequest))
                .thenReturn(clientPage);

        Page<ClientNoSecretDTO> result = service.getClientsSearch(clientId, clientName, organizationIpaCode, pageRequest);

        assertEquals(1, result.getTotalElements());
        assertEquals(clientNoSecretDTO, result.getContent().getFirst());
    }

    @Test
    void givenNullClientNameWhenGetClientsSearchThenCallSearchByFilters() {
        String clientId = "CLIENTID";
        String organizationIpaCode = "IPACODE";

        Pageable pageRequest = PageRequest.of(0, 1);

        ClientNoSecretDTO clientNoSecretDTO = new ClientNoSecretDTO();
        clientNoSecretDTO.setClientId(clientId);
        clientNoSecretDTO.setOrganizationIpaCode(organizationIpaCode);

        Page<ClientNoSecretDTO> clientPage =
                new PageImpl<>(List.of(clientNoSecretDTO), pageRequest, 1);

        Mockito.when(clientRepositoryMock.searchByFilters(clientId, organizationIpaCode, pageRequest))
                .thenReturn(clientPage);

        Page<ClientNoSecretDTO> result = service.getClientsSearch(clientId, null, organizationIpaCode, pageRequest);

        assertEquals(1, result.getTotalElements());
        assertEquals(clientNoSecretDTO, result.getContent().getFirst());
    }

    @Test
    void givenEmptyClientNameWhenGetClientsSearchThenCallSearchByFilters() {
        String clientId = "CLIENTID";
        String clientName = "";
        String organizationIpaCode = "IPACODE";
        Pageable pageRequest = PageRequest.of(0, 1);

        ClientNoSecretDTO dto = new ClientNoSecretDTO();
        dto.setClientId(clientId);
        dto.setClientName("ANY");
        dto.setOrganizationIpaCode(organizationIpaCode);

        Page<ClientNoSecretDTO> page = new PageImpl<>(List.of(dto), pageRequest, 1);

        Mockito.when(clientRepositoryMock.searchByFilters(clientId, organizationIpaCode, pageRequest))
                .thenReturn(page);

        Page<ClientNoSecretDTO> result = service.getClientsSearch(clientId, clientName, organizationIpaCode, pageRequest);

        assertEquals(1, result.getTotalElements());
        assertEquals(dto, result.getContent().getFirst());
    }

    @Test
    void givenClientIdWhenGenerateClientSecretThenInvokeClientService() {
        String organizationIpaCode = "organizationIpaCode";
        String clientId = "clientId";

        ClientDTO expectedClientDTO = ClientDTO.builder()
                .clientId(clientId)
                .clientName("Test Client")
                .organizationIpaCode(organizationIpaCode)
                .clientSecret("generatedSecret")
                .build();

        Mockito.when(clientServiceMock.generateClientSecret(organizationIpaCode, clientId))
                .thenReturn(Optional.of(expectedClientDTO));

        Optional<ClientDTO> result = service.generateClientSecret(organizationIpaCode, clientId);

        assertTrue(result.isPresent());
        assertEquals(expectedClientDTO, result.get());
    }
}
