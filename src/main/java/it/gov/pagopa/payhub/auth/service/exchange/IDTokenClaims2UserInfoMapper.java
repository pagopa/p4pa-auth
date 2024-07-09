package it.gov.pagopa.payhub.auth.service.exchange;

import com.auth0.jwt.interfaces.Claim;
import io.jsonwebtoken.Claims;
import it.gov.pagopa.payhub.auth.dto.IamUserInfoDTO;
import it.gov.pagopa.payhub.auth.dto.IamUserOrganizationRolesDTO;
import it.gov.pagopa.payhub.auth.exception.custom.InvalidOrganizationAccessDataException;
import it.gov.pagopa.payhub.auth.exception.custom.InvalidTokenException;
import it.gov.pagopa.payhub.auth.exception.custom.UserNotFoundException;
import it.gov.pagopa.payhub.auth.model.User;
import it.gov.pagopa.payhub.auth.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@Service
public class IDTokenClaims2UserInfoMapper implements Function<Map<String, Claim>, IamUserInfoDTO> {

    private final boolean organizationAccessMode;
    private final UsersRepository usersRepository;

    public IDTokenClaims2UserInfoMapper(
            @Value("${app.enable-access-organization-mode}") boolean organizationAccessMode, UsersRepository usersRepository) {
        this.organizationAccessMode = organizationAccessMode;
        this.usersRepository = usersRepository;
    }

    @Override
    public IamUserInfoDTO apply(Map<String, Claim> claims) {
        try {
            return IamUserInfoDTO.builder()
                    .issuer(claims.get(Claims.ISSUER).asString())
                    .userId(claims.get("uid").asString())
                    .name(claims.get("name").asString())
                    .familyName(claims.get("family_name").asString())
                    .email(claims.get("email").asString())
                    .fiscalCode(claims.get("fiscal_number").asString())
                    .organizationAccess(buildUserOrganizationRoles(claims))
                    .build();
        } catch (Exception e){
            throw new InvalidTokenException("Unexpected IDToken structure", e);
        }
    }

    public IamUserInfoDTO buildIamUserTestInfo(String mappedExternalUserId) {
        User userInfo = usersRepository.findByMappedExternalUserId(mappedExternalUserId)
                .orElseThrow(() -> new UserNotFoundException("User with this mappedExternalUserId not found"));
        return IamUserInfoDTO.builder()
                .userId(mappedExternalUserId)
                .name(userInfo.getFirstName())
                .familyName(userInfo.getLastName())
                .email(userInfo.getEmail())
                .fiscalCode(userInfo.getUserCode())
                .build();
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
