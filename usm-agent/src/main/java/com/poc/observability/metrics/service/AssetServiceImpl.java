package com.poc.observability.metrics.service;

import com.poc.observability.metrics.client.AssetClient;
import com.poc.observability.metrics.dto.AssetRequest;
import com.poc.observability.metrics.dto.AssetResponse;
import com.poc.observability.metrics.entity.AssetEntity;
import com.poc.observability.metrics.exception.AssetCreationException;
import com.poc.observability.metrics.exception.AssetNotFoundException;
import com.poc.observability.metrics.exception.ExternalServiceException;
import com.poc.observability.metrics.repository.AssetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssetServiceImpl implements AssetService {

    private final AssetClient assetClient;
    private final AssetRepository assetRepository;

    @Override
    public AssetResponse processAsset(String assetName, String assetType, String environment) {
        AssetRequest request = AssetRequest.builder()
                .assetName(assetName)
                .assetType(assetType)
                .environment(environment)
                .build();

        log.info("Received request for /usm-agent");
        log.info("Generated dummy asset: {}", request);

        try {
            AssetResponse existingAsset = assetClient.searchAsset(assetName);
            saveAsset(existingAsset);
            log.info("Asset found successfully: {}", existingAsset);
            return existingAsset;
        } catch (AssetNotFoundException ex) {
            log.info("Asset not found. Calling create-asset API");
            try {
                AssetResponse createdAsset = assetClient.createAsset(request);
                saveAsset(createdAsset);
                log.info("Asset created successfully: {}", createdAsset);
                return createdAsset;
            } catch (ExternalServiceException createEx) {
                log.error("Failed to process asset creation request", createEx);
                throw new AssetCreationException("Failed to create or retrieve asset", createEx);
            }
        } catch (ExternalServiceException ex) {
            log.error("Failed to search asset from external service for assetName={}", assetName, ex);
            throw new AssetCreationException("Failed to create or retrieve asset", ex);
        }
    }

    @Override
    public AssetResponse getAssetByName(String assetName) {
        log.info("Searching local asset repository for assetName={}", assetName);
        AssetEntity entity = assetRepository.findByAssetName(assetName)
                .orElseThrow(() -> new AssetNotFoundException("Asset not found for assetName=" + assetName));

        return AssetResponse.builder()
                .assetName(entity.getAssetName())
                .assetType(entity.getAssetType())
                .environment(entity.getEnvironment())
                .build();
    }

    @Override
    public AssetResponse createAsset(AssetRequest assetRequest) {
        log.info("Create API request: {}", assetRequest);
        try {
            AssetResponse createdAsset = assetClient.createAsset(assetRequest);
            saveAsset(createdAsset);
            return createdAsset;
        } catch (Exception ex) {
            log.error("Error creating asset: {}", assetRequest, ex);
            throw new AssetCreationException("Failed to create or retrieve asset", ex);
        }
    }

    private void saveAsset(AssetResponse asset) {
        assetRepository.findByAssetName(asset.getAssetName())
                .orElseGet(() -> assetRepository.save(AssetEntity.builder()
                        .assetName(asset.getAssetName())
                        .assetType(asset.getAssetType())
                        .environment(asset.getEnvironment())
                        .build()));
    }
}
