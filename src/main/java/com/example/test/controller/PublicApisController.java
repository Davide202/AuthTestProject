package com.example.test.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.ws.rs.Produces;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Produces(MediaType.APPLICATION_JSON_VALUE)
@RequestMapping("/public")
public class PublicApisController {



    @Operation(summary= "Gets status info", description= "Gets status info")
    @ApiResponse(responseCode = "200",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Map.class)))
    @GetMapping("/status")
    public ResponseEntity<Map<String,Object>> publicApis() {
        return ResponseEntity.ok(Map.of("status","up"));
    }





    @Operation(summary= "Gets auth info", description= "Gets auth info")
    @ApiResponse(responseCode = "200",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Authentication.class)))
    @GetMapping("/info")
    public ResponseEntity<Authentication> authenticated(Authentication authentication) {
        return ResponseEntity.ok(authentication);
    }




}
