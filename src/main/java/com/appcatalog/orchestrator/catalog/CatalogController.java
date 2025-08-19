package com.appcatalog.orchestrator.catalog;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/catalog")
public class CatalogController {

    @GetMapping("/services")
    public ResponseEntity<List<String>> getServices() {
        // TODO: Implement logic to fetch services from Nexus
        return ResponseEntity.ok(Collections.singletonList("sample-service"));
    }
}
