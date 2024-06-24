package it.gov.pagopa.payhub.auth.service.exchange;

import com.auth0.jwt.interfaces.Claim;
import io.jsonwebtoken.Claims;
import it.gov.pagopa.payhub.auth.exception.custom.InvalidTokenException;
import it.gov.pagopa.payhub.model.generated.UserInfo;
import it.gov.pagopa.payhub.model.generated.UserOrganizationRoles;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@Service
public class IDTokenClaims2UserInfoMapper implements Function<Map<String, Claim>, UserInfo> {
    @Override
    public UserInfo apply(Map<String, Claim> claims) {
        try {
            UserOrganizationRoles organizationRoles = buildUserOrganizationRoles(claims);
            return UserInfo.builder()
                    .issuer(claims.get(Claims.ISSUER).asString())
                    .userId(claims.get("uid").asString())
                    .name(claims.get("name").asString())
                    .familyName(claims.get("family_name").asString())
                    .fiscalCode(claims.get("fiscal_number").asString())
                    .organizationAccess(organizationRoles.getIpaCode())
                    .organizations(List.of(organizationRoles))
                    .build();
        } catch (Exception e){
            throw new InvalidTokenException("Unexpected IDToken structure", e);
        }
    }

    private UserOrganizationRoles buildUserOrganizationRoles(Map<String, Claim> claims) {
        Map<String, Object> organizationClaim = claims.get("organization").asMap();

        List<String> roles = readUserOrganizationRoles(organizationClaim);
        return UserOrganizationRoles.builder()
                .id((String)organizationClaim.get("id"))
                .name((String)organizationClaim.get("name"))
                .fiscalCode((String)organizationClaim.get("fiscal_code"))
                .ipaCode((String)organizationClaim.get("ipaCode"))
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
