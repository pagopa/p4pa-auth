package it.gov.pagopa.payhub.auth.service.user;

import it.gov.pagopa.payhub.auth.dto.IamUserInfoDTO;
import it.gov.pagopa.payhub.model.generated.UserInfo;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class IamUserInfoDTO2UserInfoMapper implements Function<IamUserInfoDTO, UserInfo> {
    @Override
    public UserInfo apply(IamUserInfoDTO iamUserInfoDTO) {
        return null; // TODO
    }
}
