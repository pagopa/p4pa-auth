package it.gov.pagopa.payhub.auth.exception.custom;

public class ClientUnauthorizedException extends RuntimeException {
    public ClientUnauthorizedException(String message){
        super(message);
    }
}
