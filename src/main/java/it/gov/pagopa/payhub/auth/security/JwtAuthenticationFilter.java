package it.gov.pagopa.payhub.auth.security;

import it.gov.pagopa.payhub.auth.exception.custom.InvalidAccessTokenException;
import it.gov.pagopa.payhub.auth.exception.custom.InvalidTokenException;
import it.gov.pagopa.payhub.auth.exception.custom.TokenExpiredException;
import it.gov.pagopa.payhub.auth.service.AccessTokenBuilderService;
import it.gov.pagopa.payhub.auth.service.AuthnService;
import it.gov.pagopa.payhub.auth.service.ValidateTokenService;
import it.gov.pagopa.payhub.auth.service.m2m.legacy.JWTLegacyHandlerService;
import it.gov.pagopa.payhub.dto.generated.UserInfo;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final AuthnService authnService;
    private final ValidateTokenService validateTokenService;
    private final JWTLegacyHandlerService jwtLegacyHandlerService;
    private final AccessTokenBuilderService accessTokenBuilderService;

	public JwtAuthenticationFilter(AuthnService authnService, ValidateTokenService validateTokenService, JWTLegacyHandlerService jwtLegacyHandlerService, AccessTokenBuilderService accessTokenBuilderService) {
		this.authnService = authnService;
		this.validateTokenService = validateTokenService;
		this.jwtLegacyHandlerService = jwtLegacyHandlerService;
		this.accessTokenBuilderService = accessTokenBuilderService;
	}

	@Override
    protected void doFilterInternal(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull FilterChain filterChain) throws ServletException, IOException {
        try {
            String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (StringUtils.hasText(authorization)) {
                String token = authorization.replace("Bearer ", "");
                UserInfo userInfo = validateToken(token);
                MDC.put("externalUserId", userInfo.getMappedExternalUserId());
                Collection<? extends GrantedAuthority> authorities = null;
                if (userInfo.getOrganizationAccess() != null) {
                    authorities = userInfo.getOrganizations().stream()
                            .filter(o -> userInfo.getOrganizationAccess().equals(o.getOrganizationIpaCode()))
                            .flatMap(r -> r.getRoles().stream())
                            .map(SimpleGrantedAuthority::new)
                            .toList();
                }
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userInfo, token, authorities);
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        } catch (Exception e){
            if(e instanceof InvalidAccessTokenException || e instanceof InvalidTokenException || e instanceof TokenExpiredException){
                log.info("An invalid accessToken has been provided: " + e.getMessage());
                response.getWriter().write(e.getMessage());
            } else {
                log.error("Something gone wrong while validate accessToken", e);
            }
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return;
        }
        filterChain.doFilter(request, response);
    }

    private UserInfo validateToken(String token) {
        if (!token.startsWith(accessTokenBuilderService.getHeaderPrefix()))
            return jwtLegacyHandlerService.handleLegacyToken(token);

        validateTokenService.validate(token);
        return authnService.getUserInfo(token);
    }
}