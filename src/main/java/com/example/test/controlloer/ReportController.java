package com.example.test.controlloer;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @GetMapping("/secret")
    @PreAuthorize("hasRole('WSO2_MANAGER')") // Funziona se il token ha il gruppo "WSO2_MANAGER"
    public String getSecretData() {
        return "Dati riservati per i manager di WSO2";
    }

    @GetMapping("/debug")
    public Map<String, Object> debugToken(@AuthenticationPrincipal Jwt jwt) {
        // Restituisce tutti i claim contenuti nel token JWT per vedere i nomi corretti
        return jwt.getClaims();
    }
}
