package it.gov.pagopa.payhub.auth.service.exchange;

import it.gov.pagopa.payhub.model.generated.AccessToken;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AccessTokenBuilderService {

    public AccessToken build(Map<String, String> subjectTokenClaims){
        return null; //TODO
    }
}
