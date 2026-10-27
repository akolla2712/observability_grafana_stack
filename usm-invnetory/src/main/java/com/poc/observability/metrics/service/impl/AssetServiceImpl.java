package com.poc.observability.metrics.service.impl;

import com.poc.observability.metrics.api.dto.AssetRequest;
import com.poc.observability.metrics.api.dto.AssetResponse;
import com.poc.observability.metrics.data.Neo4jAssetDataAccess;
import com.poc.observability.metrics.domain.entity.Asset;
import com.poc.observability.metrics.exception.AssetCreationException;
import com.poc.observability.metrics.exception.AssetNotFoundException;
import com.poc.observability.metrics.service.AssetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssetServiceImpl implements AssetService {

    private final Neo4jAssetDataAccess neo4jAssetDataAccess;

    @Override
    public AssetResponse createAsset(AssetRequest request) {
        MDC.put("correlationId", UUID.randomUUID().toString());
        MDC.put("requestId", UUID.randomUUID().toString());
        log.info("Creating asset in Neo4j={}", request);

        try {
            Asset asset = Asset.builder()
                    .id(request.getAssetName())
                    .name(request.getAssetName())
                    .assetType(request.getAssetType())
                    .environment(request.getEnvironment())
                    .build();

            Asset created = neo4jAssetDataAccess.createAsset(asset);

            AssetResponse response = AssetResponse.builder()
                    .status("SUCCESS")
                    .message("Asset processed successfully")
                    .asset(new AssetResponse.AssetDetails(created.getName(), created.getAssetType(), created.getEnvironment()))
                    .build();

            log.info("Asset created successfully={}", created);
            return response;
        } catch (Exception ex) {
            log.error("Failed to create asset={}", request, ex);
            throw new AssetCreationException("Failed to create asset", ex);
        }
    }

    @Override
    public AssetResponse searchAsset(String assetName) {
        MDC.put("correlationId", UUID.randomUUID().toString());
        MDC.put("requestId", UUID.randomUUID().toString());
        log.info("Searching asset in Neo4j for assetName={}", assetName);

        try {
            Asset asset = neo4jAssetDataAccess.findByName(assetName)
                    .orElseThrow(() -> new AssetNotFoundException("Asset not found for name: " + assetName));

            AssetResponse response = AssetResponse.builder()
                    .status("SUCCESS")
                    .message("Asset processed successfully")
                    .asset(new AssetResponse.AssetDetails(asset.getName(), asset.getAssetType(), asset.getEnvironment()))
                    .build();

            log.info("Asset found successfully={}", asset);
            return response;
        } catch (AssetNotFoundException ex) {
            log.error("Asset not found for assetName={}", assetName, ex);
            throw ex;
        } catch (Exception ex) {
            log.error("Failed to retrieve asset={}", assetName, ex);
            throw new AssetCreationException("Failed to retrieve asset", ex);
        }
    }
}
