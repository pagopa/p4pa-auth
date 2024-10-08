package it.gov.pagopa.payhub.auth.security;

import it.gov.pagopa.payhub.auth.exception.custom.InvalidAccessTokenException;
import it.gov.pagopa.payhub.auth.service.AuthnService;
import it.gov.pagopa.payhub.auth.service.ValidateTokenService;
import it.gov.pagopa.payhub.model.generated.UserInfo;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
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

    public JwtAuthenticationFilter(AuthnService authnService, ValidateTokenService validateTokenService) {
        this.authnService = authnService;
        this.validateTokenService = validateTokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (StringUtils.hasText(authorization)) {
                String token = authorization.replace("Bearer ", "");
                validateTokenService.validate(token);
                UserInfo userInfo = authnService.getUserInfo(token);
                Collection<? extends GrantedAuthority> authorities = null;
                if (userInfo.getOrganizationAccess() != null) {
                    authorities = userInfo.getOrganizations().stream()
                            .filter(o -> userInfo.getOrganizationAccess().equals(o.getOrganizationIpaCode()))
                            .flatMap(r -> r.getRoles().stream())
                            .map(SimpleGrantedAuthority::new)
                            .toList();
                }
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userInfo, null, authorities);
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        } catch (InvalidAccessTokenException e){
            log.info("An invalid accessToken has been provided: " + e.getMessage());
        } catch (Exception e){
            log.error("Something gone wrong while retrieving UserInfo", e);
        }

        filterChain.doFilter(request, response);
    }

}