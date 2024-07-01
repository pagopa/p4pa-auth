package it.gov.pagopa.payhub.auth.service.user.registration;

import org.springframework.stereotype.Service;

@Service
public class FiscalCodeObfuscatorService {
    public String obfuscate(String fiscalCode){
        char[] arr = fiscalCode.toCharArray();
        if(arr.length >= 6){
            arr[1]='X';
            arr[4]='X';
        }
        if(arr.length >= 16){
            arr[7]='X';
            arr[11]='X';
            arr[12]='X';
            arr[13]='X';
            arr[14]='X';
            arr[15]='X';
        }
        return new String(arr);
    }
}
