package it.gov.pagopa.payhub.auth.exception.custom;

public class UserUnauthorizedException extends RuntimeException {
    public UserUnauthorizedException(String message){
        super(message);
    }
}
