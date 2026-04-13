package com.example.test.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.ws.rs.Produces;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Produces(MediaType.APPLICATION_JSON_VALUE)
@RequestMapping("/auth")
public class UserApisController {



    @Operation(summary= "Api restricted to user role ROLE", description= "")
    @ApiResponse(responseCode = "200",
            content = @Content(
                    mediaType = "text/plain",
                    schema = @Schema(implementation = String.class)))
    @GetMapping("/user")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> sayHello() {
        return ResponseEntity.ok("Ciao Utente!");
    }



}
