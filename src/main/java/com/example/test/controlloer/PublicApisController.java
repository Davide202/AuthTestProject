package com.example.test.controlloer;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/public")
public class PublicApisController {

    @Operation(summary= "Gets status info", description= "Gets status info")
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = Map.class)))
    @GetMapping("/status")
    public ResponseEntity<?> publicApis() {
        return ResponseEntity.ok(
                Map.of("status","up")
        );
    }
}
