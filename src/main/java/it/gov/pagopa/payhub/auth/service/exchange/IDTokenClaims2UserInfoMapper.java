package it.gov.pagopa.payhub.auth.service.exchange;

import com.auth0.jwt.interfaces.Claim;
import io.jsonwebtoken.Claims;
import it.gov.pagopa.payhub.auth.dto.IamUserInfoDTO;
import it.gov.pagopa.payhub.auth.dto.IamUserOrganizationRolesDTO;
import it.gov.pagopa.payhub.auth.exception.custom.InvalidOrganizationAccessDataException;
import it.gov.pagopa.payhub.auth.exception.custom.InvalidTokenException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@Service
public class IDTokenClaims2UserInfoMapper implements Function<Map<String, Claim>, IamUserInfoDTO> {

    private final boolean organizationAccessMode;

    public IDTokenClaims2UserInfoMapper(
            @Value("${app.enable-access-organization-mode}") boolean organizationAccessMode) {
        this.organizationAccessMode = organizationAccessMode;
    }

    @Override
    public IamUserInfoDTO apply(Map<String, Claim> claims) {
        try {
            return IamUserInfoDTO.builder()
                    .issuer(claims.get(Claims.ISSUER).asString())
                    .userId(claims.get("uid").asString())
                    .name(claims.get("name").asString())
                    .familyName(claims.get("family_name").asString())
                    .fiscalCode(claims.get("fiscal_number").asString())
                    .organizationAccess(buildUserOrganizationRoles(claims))
                    .build();
        } catch (Exception e){
            throw new InvalidTokenException("Unexpected IDToken structure", e);
        }
    }

    private IamUserOrganizationRolesDTO buildUserOrganizationRoles(Map<String, Claim> claims) {
        Claim organization = claims.get("organization");
        if(organization==null){
            if(organizationAccessMode){
                throw new InvalidOrganizationAccessDataException("No organizationAccess information");
            } else {
                return null;
            }
        }

        Map<String, Object> organizationClaim = organization.asMap();

        List<String> roles = readUserOrganizationRoles(organizationClaim);
        return IamUserOrganizationRolesDTO.builder()
                .organizationIpaCode((String)organizationClaim.get("ipaCode"))
                .email(claims.get("email").asString())
                .roles(roles)
                .build();
    }

    private static List<String> readUserOrganizationRoles(Map<String, Object> organizationClaim) {
        List<String> out;
        if(organizationClaim.get("roles") instanceof List<?> roles){
            out = roles.stream().map(o -> (o instanceof Map<?, ?> orgMap) ? (String) orgMap.get("role") : null)
                    .filter(Objects::nonNull)
                    .toList();
        } else {
            out = List.of();
        }
        if(out.isEmpty()){
            throw new InvalidTokenException("No organization roles provided");
        } else {
            return out;
        }
    }
}
