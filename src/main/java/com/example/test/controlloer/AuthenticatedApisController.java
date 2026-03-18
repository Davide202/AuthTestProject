package com.example.test.controlloer;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthenticatedApisController {


    @Operation(summary= "Gets auth info", description= "Gets auth info")
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = Authentication.class)))
    @GetMapping("/info")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')") //@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Authentication> authenticated(Authentication authentication) {
        return ResponseEntity.ok(authentication);
    }

    @GetMapping("/hello")
    @PreAuthorize("hasRole('USER')")
    public String sayHello() {
        return "Ciao Utente!";
    }

    @GetMapping("/secret")
    @PreAuthorize("hasRole('ADMIN')")
    public String saySecret() {
        return "Area Riservata agli Admin";
    }
}
