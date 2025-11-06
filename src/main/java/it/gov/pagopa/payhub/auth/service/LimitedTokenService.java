package it.gov.pagopa.payhub.auth.service;

import it.gov.pagopa.payhub.dto.generated.AccessToken;
import it.gov.pagopa.payhub.dto.generated.LimitedTokenRequest;

public interface LimitedTokenService {
    AccessToken build(LimitedTokenRequest request);
}
