package com.example.test.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.ws.rs.Produces;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Produces(MediaType.APPLICATION_JSON_VALUE)
@RequestMapping("/api/reports")
public class Wso2ManagerController {



    @Operation(summary= "Api restricted to user role WSO2_MANAGER", description= "")
    @ApiResponse(responseCode = "200",
            content = @Content(
                    mediaType = "text/plain",
                    schema = @Schema(implementation = String.class)))
    @GetMapping("/secret")
    @PreAuthorize("hasRole('WSO2_MANAGER')") // Funziona se il token ha il gruppo "WSO2_MANAGER"
    public ResponseEntity<String> getSecretData() {
        return ResponseEntity.ok("Dati riservati per i manager di WSO2");
    }





}
