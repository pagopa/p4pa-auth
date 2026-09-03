package it.gov.pagopa.payhub.auth.service;

import com.auth0.jwt.HeaderParams;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import io.jsonwebtoken.security.Jwks;
import io.jsonwebtoken.security.PublicJwk;
import it.gov.pagopa.payhub.auth.dto.IamUserInfoDTO;
import it.gov.pagopa.payhub.auth.utils.CertUtils;
import it.gov.pagopa.payhub.dto.generated.AccessToken;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class AccessTokenBuilderService {
    public static final String ACCESS_TOKEN_TYPE = "JWT";
    public static final String REFRESH_TOKEN_TYPE = "refresh_token";
    public static final String CLAIM_ORGANIZATION_IPA_CODE = "organizationIpaCode";
    private final String allowedAudience;
    private final int expireIn;
    private final int refreshExpireIn;
    private final Algorithm algorithm;
    private final String kid;
    @Getter
    private final PublicJwk<?> jwk;

    public AccessTokenBuilderService(
            @Value("${jwt.audience}") String allowedAudience,
            @Value("${jwt.access-token.expire-in}") int expireIn,
            @Value("${jwt.access-token.private-key}") String privateKey,
            @Value("${jwt.access-token.public-key}") String publicKey, DataCipherService dataCipherService,
            @Value("${jwt.refresh-token.expire-in}") int refreshExpireIn) {
        this.allowedAudience = allowedAudience;
        this.expireIn = expireIn;
        this.refreshExpireIn = refreshExpireIn;
        byte[] hashed = dataCipherService.hash(publicKey.replace("\n", ""));
        this.kid = UUID.nameUUIDFromBytes(hashed).toString();

        try {
            RSAPrivateKey rsaPrivateKey = CertUtils.pemKey2PrivateKey(privateKey);
            RSAPublicKey rsaPublicKey = CertUtils.pemPub2PublicKey(publicKey);

            algorithm = Algorithm.RSA512(rsaPublicKey, rsaPrivateKey);

            jwk = Jwks.builder()
                    .id(kid)
                    .algorithm(algorithm.getName())
                    .key(rsaPublicKey)
                    .publicKeyUse("sign")
                    .build();
        } catch (InvalidKeySpecException | NoSuchAlgorithmException | IOException e) {
            throw new IllegalStateException("Cannot load private and/or public key", e);
        }

    }

    public AccessToken build(IamUserInfoDTO iamUserInfoDTO) {
        return build(iamUserInfoDTO, expireIn, refreshExpireIn, true);
    }

    public AccessToken build(IamUserInfoDTO iamUserInfoDTO, Integer expireInParam, Integer refreshExpireInParam, boolean generateRefreshToken) {
        Map<String, Object> headerClaims = new HashMap<>();
        headerClaims.put(HeaderParams.KEY_ID, kid);
        headerClaims.put("typ", ACCESS_TOKEN_TYPE);
        String tokenType = "bearer";
        JWTCreator.Builder jwtBuilder = JWT.create()
                .withHeader(headerClaims)
                .withClaim("typ", tokenType)
                .withIssuer(allowedAudience)
                .withJWTId(UUID.randomUUID().toString())
                .withSubject(iamUserInfoDTO.getMappedExternalUserId())
                .withIssuedAt(Instant.now())
                .withExpiresAt(Instant.now().plusSeconds(expireInParam == null ? expireIn : expireInParam));
        if(iamUserInfoDTO.getOrganizationAccess()!=null){
            jwtBuilder.withClaim(CLAIM_ORGANIZATION_IPA_CODE, iamUserInfoDTO.getOrganizationAccess().getOrganizationIpaCode());
        }
        if (iamUserInfoDTO.getResource() != null) {
            jwtBuilder.withClaim("scope", iamUserInfoDTO.getResource().getApp());
        }
        String token = jwtBuilder
                .sign(algorithm);

        if (iamUserInfoDTO.getIssueAt()== null) {
            iamUserInfoDTO.setIssueAt(Instant.now().getEpochSecond());
        }

        if(generateRefreshToken) {
            int actualRefreshExpireIn = (refreshExpireInParam != null) ? refreshExpireInParam : refreshExpireIn;
            String refreshTokenStr = JWT.create()
                    .withHeader(headerClaims)
                    .withClaim("typ", REFRESH_TOKEN_TYPE)
                    .withIssuer(allowedAudience)
                    .withJWTId(UUID.randomUUID().toString())
                    .withSubject(iamUserInfoDTO.getMappedExternalUserId())
                    .withIssuedAt(Instant.now())
                    .withExpiresAt(Instant.now().plusSeconds(actualRefreshExpireIn))
                    .sign(algorithm);

            return new AccessToken(token, tokenType, expireInParam == null ? expireIn : expireInParam, refreshTokenStr, refreshExpireIn);
        }


        return new AccessToken(token, tokenType, expireInParam == null ? expireIn : expireInParam, null, null);
    }

    public String getHeaderPrefix() {
        String prefix = String.format("{\"kid\":\"%s\"", kid);
        return Base64.getEncoder().encodeToString(prefix.getBytes());
    }

}
