package com.poc.observability.metrics.api.controller;

import com.poc.observability.metrics.api.dto.AssetRequest;
import com.poc.observability.metrics.api.dto.AssetResponse;
import com.poc.observability.metrics.service.AssetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
@Slf4j
public class AssetController {

    private final AssetService assetService;

    @PostMapping("/create-asset")
    public ResponseEntity<AssetResponse> createAsset(@Valid @RequestBody AssetRequest request) {
        MDC.put("correlationId", UUID.randomUUID().toString());
        MDC.put("requestId", UUID.randomUUID().toString());
        log.info("Received request for create-asset with payload={}", request);

        AssetResponse response = assetService.createAsset(request);

        log.info("Create asset response status={} message={} asset={}", response.getStatus(), response.getMessage(), response.getAsset());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/search-asset")
    public ResponseEntity<AssetResponse> searchAsset(@RequestParam String assetName) {
        MDC.put("correlationId", UUID.randomUUID().toString());
        MDC.put("requestId", UUID.randomUUID().toString());
        log.info("Received request for search-asset with assetName={}", assetName);

        AssetResponse response = assetService.searchAsset(assetName);

        log.info("Search asset response status={} message={} asset={}", response.getStatus(), response.getMessage(), response.getAsset());
        return ResponseEntity.ok(response);
    }
}
