package com.example.test.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.Produces;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Produces(MediaType.APPLICATION_JSON_VALUE)
@RequestMapping("/auth")
@Tag(name = "3. Admin APIs", description = "Endpoint ristrette agli utenti con ruolo ADMIN")
public class AdminApisController {


    @Operation(
            summary= "Api restricted to user role ADMIN",
            description= "Empty description"
    )
    @ApiResponse(responseCode = "200",
            content = @Content(
                    mediaType = "text/plain",
                    schema = @Schema(implementation = String.class)))
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> saySecret() {
        return ResponseEntity.ok("Area Riservata agli Admin");


    }

/*

    @Operation(
            summary= "Gets auth info",
            description= "This API descripts the Authentication Context"
    )
    @ApiResponse(responseCode = "200",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Authentication.class)))
    @GetMapping("/info")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')") //@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Authentication> authenticated(Authentication authentication) {
        return ResponseEntity.ok(authentication);
    }
*/



}
