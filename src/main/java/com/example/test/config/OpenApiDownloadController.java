package com.example.test.config;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Hidden
@RestController
@Tag(name = "Utility OpenAPI", description = "Endpoint per il download della specifica")
public class OpenApiDownloadController {

    private static final String CONTENT_DISPOSITION = "Content-Disposition";
    private static final String FILE_NAME_JSON = "attachment; filename=\"%s.json" + "\"";
    private static final String FILE_NAME_YAML = "attachment; filename=\"%s.yaml" + "\"";

    @Value("${springdoc.api-docs.path}")
    private String apiDocsPath;

    @Value("${springdoc.api-docs.fileName}")
    private String fileName;

    @GetMapping("${springdoc.api-docs.path}.json")
    public void downloadJsonDocs(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setHeader(CONTENT_DISPOSITION, FILE_NAME_JSON.formatted(fileName));
        request.getRequestDispatcher(apiDocsPath).forward(request, response);
    }

    @GetMapping("${springdoc.api-docs.path}.yml")
    public void downloadYamlDocs(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setHeader(CONTENT_DISPOSITION, FILE_NAME_YAML.formatted(fileName));
        request.getRequestDispatcher(apiDocsPath+".yaml").forward(request, response);
    }


    @GetMapping
    public ResponseEntity<Map<String,Object>> status() {
        return ResponseEntity.ok(Map.of("status","up"));
    }
}
