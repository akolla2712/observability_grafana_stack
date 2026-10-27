package com.poc.observability.metrics;

import com.poc.observability.metrics.api.dto.AssetRequest;
import com.poc.observability.metrics.api.dto.AssetResponse;
import com.poc.observability.metrics.data.Neo4jAssetDataAccess;
import com.poc.observability.metrics.domain.entity.Asset;
import com.poc.observability.metrics.exception.AssetNotFoundException;
import com.poc.observability.metrics.service.impl.AssetServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AssetServiceTest {

    @Mock
    private Neo4jAssetDataAccess neo4jAssetDataAccess;

    @InjectMocks
    private AssetServiceImpl assetService;

    @Test
    void shouldCreateAssetSuccessfully() {
        AssetRequest request = new AssetRequest("Asset A", "APPLICATION", "DEV");
        Asset asset = Asset.builder().name("Asset A").assetType("APPLICATION").environment("DEV").build();
        when(neo4jAssetDataAccess.createAsset(any(Asset.class))).thenReturn(asset);

        AssetResponse result = assetService.createAsset(request);

        assertNotNull(result);
        assertEquals("SUCCESS", result.getStatus());
        assertEquals("Asset A", result.getAsset().getAssetName());
    }

    @Test
    void shouldThrowWhenAssetNotFound() {
        when(neo4jAssetDataAccess.findByName("Missing Asset")).thenReturn(Optional.empty());

        AssetNotFoundException exception = assertThrows(AssetNotFoundException.class,
                () -> assetService.searchAsset("Missing Asset"));

        assertEquals("Asset not found for name: Missing Asset", exception.getMessage());
    }
}
