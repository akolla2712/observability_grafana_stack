package com.poc.observability.metrics.controller;

import com.poc.observability.metrics.dto.ApiResponse;
import com.poc.observability.metrics.service.AssetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AssetController {

  private final AssetService assetService;

  @GetMapping("/usm-agent")
  public ResponseEntity<ApiResponse> getAsset() {
    log.info("Received request for /usm-agent");

    List<String> assetNames = generateData();
    String resolvedAssetType = "APPLICATION";
    String resolvedEnvironment = "DEV";

    for (String resolvedAssetName : assetNames) {
      assetService.processAsset(resolvedAssetName, resolvedAssetType, resolvedEnvironment);
    }

    return ResponseEntity.ok(
        ApiResponse.builder().status("SUCCESS").message("Asset processed successfully").build());
  }

  private List<String> generateData() {
    List<String> names = new ArrayList<>();
    String[] prefixes = {
      "Alpha",
      "Beta",
      "Gamma",
      "Delta",
      "Omega",
      "Sigma",
      "Nova",
      "Echo",
      "Prime",
      "Luna"
    };

    String[] suffixes = {
      "Asset",
      "Service",
      "App",
      "Cluster",
      "DB",
      "Gateway",
      "Node",
      "System",
      "Infra",
      "Worker"
    };

    Random random = new Random();

    for (int i = 0; i < 5; i++) {
      String name = prefixes[random.nextInt(prefixes.length)]
          + "-"
          + suffixes[random.nextInt(suffixes.length)]
          + "-"
          + (random.nextInt(900) + 100);
      names.add(name);
    }

    return names;
  }
}
