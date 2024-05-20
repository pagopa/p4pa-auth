package it.gov.pagopa.payhub.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payhub")
interface AuthController {

    @GetMapping("/auth")
    @ResponseStatus(code = HttpStatus.OK)
    void authToken(@RequestParam String token);
}