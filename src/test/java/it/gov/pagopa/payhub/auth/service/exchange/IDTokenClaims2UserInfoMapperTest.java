package it.gov.pagopa.payhub.auth.service.exchange;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.Claim;
import it.gov.pagopa.payhub.auth.exception.custom.InvalidTokenException;
import it.gov.pagopa.payhub.model.generated.UserInfo;
import it.gov.pagopa.payhub.model.generated.UserOrganizationRoles;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Collections;
import java.util.List;
import java.util.Map;

class IDTokenClaims2UserInfoMapperTest {

    private IDTokenClaims2UserInfoMapper mapper;

    void init(boolean isOrganizationAccessMode){
        mapper = new IDTokenClaims2UserInfoMapper(isOrganizationAccessMode);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void givenEmptyClaimsThenInvalidTokenException(boolean isOrganizationAccess){
        init(isOrganizationAccess);
        Map<String, Claim> claims = Collections.emptyMap();
        Assertions.assertThrows(InvalidTokenException.class, () -> mapper.apply(claims));
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void givenIDTokenClaimsThenOk(boolean isOrganizationAccess) {
        // Given
        init(isOrganizationAccess);
        UserInfo expectedUserInfo = UserInfo.builder()
                .userId("e1d9c534-86a9-4039-80da-8aa7a33ac9e7")
                .name("demo")
                .familyName("demosurname")
                .fiscalCode("DMEMPY15L21L736U")
                .issuer("https://dev.selfcare.pagopa.it")
                .organizationAccess("SELC_99999999990")
                .organizations(List.of(UserOrganizationRoles.builder()
                        .id("133e9c1b-dfc5-43ea-98a7-f64f30613074")
                        .name("Ente P4PA intermediato 1")
                        .fiscalCode("99999999990")
                        .ipaCode("SELC_99999999990")
                        .roles(List.of("ROLE_ADMIN"))
                        .build()))
                .build();
        Map<String, Claim> claims = JWT.decode("eyJraWQiOiJqd3QtZXhjaGFuZ2VfZTA6OTQ6M2Q6NWI6YWY6ODY6YWU6YWM6YzM6ZGI6OWM6MzI6NTc6NWE6YTA6NDciLCJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJ0eXAiOiJJRCIsImZhbWlseV9uYW1lIjoiZGVtb3N1cm5hbWUiLCJmaXNjYWxfbnVtYmVyIjoiRE1FTVBZMTVMMjFMNzM2VSIsIm5hbWUiOiJkZW1vIiwic3BpZF9sZXZlbCI6Imh0dHBzOi8vd3d3LnNwaWQuZ292Lml0L1NwaWRMMiIsImZyb21fYWEiOmZhbHNlLCJzdWIiOiJlMWQ5YzUzNC04NmE5LTQwMzktODBkYS04YWE3YTMzYWM5ZTciLCJ1aWQiOiJlMWQ5YzUzNC04NmE5LTQwMzktODBkYS04YWE3YTMzYWM5ZTciLCJsZXZlbCI6IkwyIiwiaWF0IjoxNzE0OTgwNTY4LCJleHAiOjU3MTQ5ODE0NjgsImF1ZCI6ImRldi5waWF0dGFmb3JtYXVuaXRhcmlhLnBhZ29wYS5pdCIsImlzcyI6Imh0dHBzOi8vZGV2LnNlbGZjYXJlLnBhZ29wYS5pdCIsImp0aSI6IjkyYzQ3OWI1LTUxMTUtNGQzMS1iMDliLTVjZmFmNTQ1Mzc3NCIsImVtYWlsIjoiZWVAZWUuaXQiLCJvcmdhbml6YXRpb24iOnsiaWQiOiIxMzNlOWMxYi1kZmM1LTQzZWEtOThhNy1mNjRmMzA2MTMwNzQiLCJuYW1lIjoiRW50ZSBQNFBBIGludGVybWVkaWF0byAxIiwicm9sZXMiOlt7InBhcnR5Um9sZSI6IkRFTEVHQVRFIiwicm9sZSI6IlJPTEVfQURNSU4ifV0sImZpc2NhbF9jb2RlIjoiOTk5OTk5OTk5OTAiLCJpcGFDb2RlIjoiU0VMQ185OTk5OTk5OTk5MCJ9LCJkZXNpcmVkX2V4cCI6NTcxNTAxMjg5NH0.I1LBk-69DTROtoLqS5wN3oHC-9W5GBUqiDe907ak5mVNwE00SnMxLFTzTPLfrSCy6zw1PaSx_Xcju2PiK6Xax6gkEz49-LeZ2KFFla1Al1bP35lrO8JRfoo7EefgMNRhXnnYUL6klVIVDpW1hz6G0Lx5p7WZZfU9Kq-hvf8cofkQs3mVIzEF_kvYtrJNug2liZmEzGqwYlgeHE0P3wWPDLKYELHfACiYgw9JMAktPmMsupyyDMfGLfJkZ62z_QovktuFYAqgV6METGwuJP0u3T3XxJy1NspsOudJo_r5r5o3oR14NOpR_PezzDO8ZhcXBRtb11v6mcuvLQD6VSPQ0A").getClaims();

        // When
        UserInfo result = mapper.apply(claims);

        // Then
        Assertions.assertEquals(expectedUserInfo, result);
    }

    @Test
    void givenIDTokenNoOrganizationAndOrganizationAccessModeThenInvalidTokenException() {
        // Given
        init(true);
        Map<String, Claim> claims = JWT.decode("eyJraWQiOiJqd3QtZXhjaGFuZ2VfZTA6OTQ6M2Q6NWI6YWY6ODY6YWU6YWM6YzM6ZGI6OWM6MzI6NTc6NWE6YTA6NDciLCJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJ0eXAiOiJJRCIsImZhbWlseV9uYW1lIjoiZGVtb3N1cm5hbWUiLCJmaXNjYWxfbnVtYmVyIjoiRE1FTVBZMTVMMjFMNzM2VSIsIm5hbWUiOiJkZW1vIiwic3BpZF9sZXZlbCI6Imh0dHBzOi8vd3d3LnNwaWQuZ292Lml0L1NwaWRMMiIsImZyb21fYWEiOmZhbHNlLCJzdWIiOiJlMWQ5YzUzNC04NmE5LTQwMzktODBkYS04YWE3YTMzYWM5ZTciLCJ1aWQiOiJlMWQ5YzUzNC04NmE5LTQwMzktODBkYS04YWE3YTMzYWM5ZTciLCJsZXZlbCI6IkwyIiwiaWF0IjoxNzE0OTgwNTY4LCJleHAiOjU3MTQ5ODE0NjgsImF1ZCI6ImRldi5waWF0dGFmb3JtYXVuaXRhcmlhLnBhZ29wYS5pdCIsImlzcyI6Imh0dHBzOi8vZGV2LnNlbGZjYXJlLnBhZ29wYS5pdCIsImp0aSI6IjkyYzQ3OWI1LTUxMTUtNGQzMS1iMDliLTVjZmFmNTQ1Mzc3NCIsImVtYWlsIjoiZWVAZWUuaXQiLCJkZXNpcmVkX2V4cCI6NTcxNTAxMjg5NH0.aJIhnNEOXNx8wDTSmAVv7NJio_J9muU2Jq9bY3AX0rL09LIvoGoxH1-u2-gdz_2LpxfZjluS0PUNKvAVgAbtK7o_vNPnThbCaQjVvtBPPBSWcjXSxbi7uJ9r89CMyu1CGim5tXz9cPB9vTBsdPElQ4xGnWGUBl8nYvUZ9oayYxDpPBdEjbHUEAvjjiaEHFjt5bmzYeyOnh4g_qe6m_j6JzsijRNXh87Ple4Awb72CPaf4gwTXIPQqoHIISSKfBYOIU_zl5mg5e-p3pTdsNNBR30vKViiolde4EIuY33IB5hSAjk5Pvw_5TD26sEI581Zra9ccaUsoPW2watGAFa6kg").getClaims();

        // When
        Assertions.assertThrows(InvalidTokenException.class, () -> mapper.apply(claims));
    }

    @Test
    void givenIDTokenNoOrganizationAndNoOrganizationAccessModeThenOk() {
        // Given
        init(false);
        UserInfo expectedUserInfo = UserInfo.builder()
                .userId("e1d9c534-86a9-4039-80da-8aa7a33ac9e7")
                .name("demo")
                .familyName("demosurname")
                .fiscalCode("DMEMPY15L21L736U")
                .issuer("https://dev.selfcare.pagopa.it")
                .build();
        Map<String, Claim> claims = JWT.decode("eyJraWQiOiJqd3QtZXhjaGFuZ2VfZTA6OTQ6M2Q6NWI6YWY6ODY6YWU6YWM6YzM6ZGI6OWM6MzI6NTc6NWE6YTA6NDciLCJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJ0eXAiOiJJRCIsImZhbWlseV9uYW1lIjoiZGVtb3N1cm5hbWUiLCJmaXNjYWxfbnVtYmVyIjoiRE1FTVBZMTVMMjFMNzM2VSIsIm5hbWUiOiJkZW1vIiwic3BpZF9sZXZlbCI6Imh0dHBzOi8vd3d3LnNwaWQuZ292Lml0L1NwaWRMMiIsImZyb21fYWEiOmZhbHNlLCJzdWIiOiJlMWQ5YzUzNC04NmE5LTQwMzktODBkYS04YWE3YTMzYWM5ZTciLCJ1aWQiOiJlMWQ5YzUzNC04NmE5LTQwMzktODBkYS04YWE3YTMzYWM5ZTciLCJsZXZlbCI6IkwyIiwiaWF0IjoxNzE0OTgwNTY4LCJleHAiOjU3MTQ5ODE0NjgsImF1ZCI6ImRldi5waWF0dGFmb3JtYXVuaXRhcmlhLnBhZ29wYS5pdCIsImlzcyI6Imh0dHBzOi8vZGV2LnNlbGZjYXJlLnBhZ29wYS5pdCIsImp0aSI6IjkyYzQ3OWI1LTUxMTUtNGQzMS1iMDliLTVjZmFmNTQ1Mzc3NCIsImVtYWlsIjoiZWVAZWUuaXQiLCJkZXNpcmVkX2V4cCI6NTcxNTAxMjg5NH0.aJIhnNEOXNx8wDTSmAVv7NJio_J9muU2Jq9bY3AX0rL09LIvoGoxH1-u2-gdz_2LpxfZjluS0PUNKvAVgAbtK7o_vNPnThbCaQjVvtBPPBSWcjXSxbi7uJ9r89CMyu1CGim5tXz9cPB9vTBsdPElQ4xGnWGUBl8nYvUZ9oayYxDpPBdEjbHUEAvjjiaEHFjt5bmzYeyOnh4g_qe6m_j6JzsijRNXh87Ple4Awb72CPaf4gwTXIPQqoHIISSKfBYOIU_zl5mg5e-p3pTdsNNBR30vKViiolde4EIuY33IB5hSAjk5Pvw_5TD26sEI581Zra9ccaUsoPW2watGAFa6kg").getClaims();

        // When
        UserInfo result = mapper.apply(claims);

        // Then
        Assertions.assertEquals(expectedUserInfo, result);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void givenIDTokenNoOrganizationRolesThenInvalidTokenException(boolean isOrganizationAccess) {
        // Given
        init(isOrganizationAccess);
        Map<String, Claim> claims = JWT.decode("eyJraWQiOiJqd3QtZXhjaGFuZ2VfZTA6OTQ6M2Q6NWI6YWY6ODY6YWU6YWM6YzM6ZGI6OWM6MzI6NTc6NWE6YTA6NDciLCJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJ0eXAiOiJJRCIsImZhbWlseV9uYW1lIjoiZGVtb3N1cm5hbWUiLCJmaXNjYWxfbnVtYmVyIjoiRE1FTVBZMTVMMjFMNzM2VSIsIm5hbWUiOiJkZW1vIiwic3BpZF9sZXZlbCI6Imh0dHBzOi8vd3d3LnNwaWQuZ292Lml0L1NwaWRMMiIsImZyb21fYWEiOmZhbHNlLCJzdWIiOiJlMWQ5YzUzNC04NmE5LTQwMzktODBkYS04YWE3YTMzYWM5ZTciLCJ1aWQiOiJlMWQ5YzUzNC04NmE5LTQwMzktODBkYS04YWE3YTMzYWM5ZTciLCJsZXZlbCI6IkwyIiwiaWF0IjoxNzE0OTgwNTY4LCJleHAiOjU3MTQ5ODE0NjgsImF1ZCI6ImRldi5waWF0dGFmb3JtYXVuaXRhcmlhLnBhZ29wYS5pdCIsImlzcyI6Imh0dHBzOi8vZGV2LnNlbGZjYXJlLnBhZ29wYS5pdCIsImp0aSI6IjkyYzQ3OWI1LTUxMTUtNGQzMS1iMDliLTVjZmFmNTQ1Mzc3NCIsImVtYWlsIjoiZWVAZWUuaXQiLCJvcmdhbml6YXRpb24iOnsiaWQiOiIxMzNlOWMxYi1kZmM1LTQzZWEtOThhNy1mNjRmMzA2MTMwNzQiLCJuYW1lIjoiRW50ZSBQNFBBIGludGVybWVkaWF0byAxIiwiZmlzY2FsX2NvZGUiOiI5OTk5OTk5OTk5MCIsImlwYUNvZGUiOiJTRUxDXzk5OTk5OTk5OTkwIn0sImRlc2lyZWRfZXhwIjo1NzE1MDEyODk0fQ.qx8bpuH4GG3B_VTkeirqZZovAPIgg9JeHhkS_fJXHgCKrL35uJfCGEcX9GuHNvOhaHPTM8nHwM7tMS8dgo0RAT7RQMQ-qwoOhhFVK0QX7D9dDe6C1sEviBaItMzpHnr5UKgavxStYI5KreLaIFSY89hyLeXIjlXizqQ4D5QHPhwfFDGFkDnKFW2h2CY5Usd4W4ebGPNRCmp9vNcfqVnvzi3lv3-SsVuCl80DyRLza5Rv-ce06pb-JI-axxu2VuLKwp2ghG8YZgiMZxNLw5Ew48MtX-5beHkDYkwJQITxaCAwv2Lj_6quLqGCsD_nbxh77aLhKSWWbjAj6DpiMBjOLA").getClaims();

        // When
        Assertions.assertThrows(InvalidTokenException.class, () -> mapper.apply(claims));
    }
}
