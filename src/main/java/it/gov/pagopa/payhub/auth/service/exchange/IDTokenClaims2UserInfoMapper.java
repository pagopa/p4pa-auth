package it.gov.pagopa.payhub.auth.service.exchange;

import com.auth0.jwt.interfaces.Claim;
import io.jsonwebtoken.Claims;
import it.gov.pagopa.payhub.auth.exception.custom.InvalidOrganizationAccessDataException;
import it.gov.pagopa.payhub.auth.exception.custom.InvalidTokenException;
import it.gov.pagopa.payhub.model.generated.UserInfo;
import it.gov.pagopa.payhub.model.generated.UserOrganizationRoles;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@Service
public class IDTokenClaims2UserInfoMapper implements Function<Map<String, Claim>, UserInfo> {

    private final boolean organizationAccessMode;

    public IDTokenClaims2UserInfoMapper(
            @Value("${app.enable-access-organization-mode}") boolean organizationAccessMode) {
        this.organizationAccessMode = organizationAccessMode;
    }

    @Override
    public UserInfo apply(Map<String, Claim> claims) {
        try {
            UserInfo userInfo = UserInfo.builder()
                    .issuer(claims.get(Claims.ISSUER).asString())
                    .userId(claims.get("uid").asString())
                    .name(claims.get("name").asString())
                    .familyName(claims.get("family_name").asString())
                    .fiscalCode(claims.get("fiscal_number").asString())
                    .build();

            UserOrganizationRoles organizationRoles = buildUserOrganizationRoles(claims);
            if(organizationRoles!=null){
                userInfo.setOrganizationAccess(organizationRoles.getIpaCode());
                userInfo.setOrganizations(List.of(organizationRoles));
            }

            return userInfo;
        } catch (Exception e){
            throw new InvalidTokenException("Unexpected IDToken structure", e);
        }
    }

    private UserOrganizationRoles buildUserOrganizationRoles(Map<String, Claim> claims) {
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
