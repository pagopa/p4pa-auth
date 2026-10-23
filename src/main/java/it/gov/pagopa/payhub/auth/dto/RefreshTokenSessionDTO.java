package it.gov.pagopa.payhub.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class RefreshTokenSessionDTO {
    private IamUserInfoDTO iamUserInfo;
    private String accessToken;
}
