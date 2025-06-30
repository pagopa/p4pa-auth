package it.gov.pagopa.payhub.auth.service.user.retrieve;

import it.gov.pagopa.payhub.auth.dto.IamUserInfoDTO;
import it.gov.pagopa.payhub.auth.exception.custom.UserNotFoundException;
import it.gov.pagopa.payhub.auth.mapper.A2ALegacyClaims2UserInfoMapper;
import it.gov.pagopa.payhub.auth.mapper.Client2UserInfoMapper;
import it.gov.pagopa.payhub.auth.mapper.ClientMapper;
import it.gov.pagopa.payhub.auth.model.User;
import it.gov.pagopa.payhub.auth.repository.ClientRepository;
import it.gov.pagopa.payhub.auth.repository.UsersRepository;
import it.gov.pagopa.payhub.auth.service.m2m.AuthorizeClientCredentialsRequestService;
import it.gov.pagopa.payhub.auth.service.user.IamUserInfoDTO2UserInfoMapper;
import it.gov.pagopa.payhub.dto.generated.ClientNoSecretDTO;
import it.gov.pagopa.payhub.dto.generated.UserInfo;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserInfoRetrieverService {

    private final UsersRepository usersRepository;
    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;
    private final Client2UserInfoMapper client2UserInfoMapper;
    private final A2ALegacyClaims2UserInfoMapper a2aLegacy2UserInfoMapper;
    private final IamUserInfoDTO2UserInfoMapper iamUserInfoMapper;

    public UserInfoRetrieverService(UsersRepository usersRepository, ClientRepository clientRepository, ClientMapper clientMapper, Client2UserInfoMapper client2UserInfoMapper, A2ALegacyClaims2UserInfoMapper a2aLegacy2UserInfoMapper, IamUserInfoDTO2UserInfoMapper iamUserInfoMapper) {
        this.usersRepository = usersRepository;
        this.clientRepository = clientRepository;
        this.clientMapper = clientMapper;
        this.client2UserInfoMapper = client2UserInfoMapper;
        this.a2aLegacy2UserInfoMapper = a2aLegacy2UserInfoMapper;
        this.iamUserInfoMapper = iamUserInfoMapper;
    }

    public UserInfo findByMappedExternalUserId(String mappedExternalUserId, String accessToken){
        IamUserInfoDTO iamUserInfo;
        if(Client2UserInfoMapper.isSystemMappedUser(mappedExternalUserId)) {
            iamUserInfo = findSystemIamUser(mappedExternalUserId);
        } else if(A2ALegacyClaims2UserInfoMapper.isA2AMappedUser(mappedExternalUserId)) {
            return a2aLegacy2UserInfoMapper.map(A2ALegacyClaims2UserInfoMapper.extractOrgIpaCode(mappedExternalUserId));
        } else {
            iamUserInfo = findIamUser(mappedExternalUserId);
        }

        return iamUserInfoMapper.apply(iamUserInfo, accessToken);
    }

    private IamUserInfoDTO findSystemIamUser(String mappedExternalUserId) {
        Optional<ClientNoSecretDTO> clientNoSecretDTO;
        String clientId = Client2UserInfoMapper.extractClientId(mappedExternalUserId);
        if(AuthorizeClientCredentialsRequestService.isPuSystemClientId(clientId)){
            clientNoSecretDTO = Optional.of(AuthorizeClientCredentialsRequestService.puSystemClientId2ClientNoSecretDTO(clientId));
        } else {
            clientNoSecretDTO = clientRepository.findById(clientId)
                    .map(clientMapper::mapToNoSecretDTO);
        }
        return clientNoSecretDTO
                .map(client2UserInfoMapper)
                .orElseThrow(() -> new UserNotFoundException("Cannot find client related to mappedExternalUserId:" + mappedExternalUserId));
    }

    private IamUserInfoDTO findIamUser(String mappedExternalUserId) {
        return usersRepository.findByMappedExternalUserId(mappedExternalUserId)
                .map(this::user2IamUser)
                .orElseThrow(() -> new UserNotFoundException("Cannot find user having mappedExternalId:" + mappedExternalUserId));
    }

    private IamUserInfoDTO user2IamUser(User user) {
        return IamUserInfoDTO.builder()
                .issuer(user.getIamIssuer())
                .systemUser(false)
                .userId(user.getUserId())
                .innerUserId(user.getUserId())
                .mappedExternalUserId(user.getMappedExternalUserId())
                .fiscalCode(user.getFiscalCode())
                .name(user.getFirstName())
                .familyName(user.getLastName())
                .build();
    }
}
