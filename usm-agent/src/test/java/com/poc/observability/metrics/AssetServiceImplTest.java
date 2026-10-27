package com.poc.observability.metrics;

import com.poc.observability.metrics.client.AssetClient;
import com.poc.observability.metrics.dto.AssetRequest;
import com.poc.observability.metrics.dto.AssetResponse;
import com.poc.observability.metrics.entity.AssetEntity;
import com.poc.observability.metrics.exception.AssetCreationException;
import com.poc.observability.metrics.exception.AssetNotFoundException;
import com.poc.observability.metrics.exception.ExternalServiceException;
import com.poc.observability.metrics.repository.AssetRepository;
import com.poc.observability.metrics.service.AssetServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AssetServiceImplTest {

    @Mock
    private AssetClient assetClient;

    @Mock
    private AssetRepository assetRepository;

    @InjectMocks
    private AssetServiceImpl assetService;

    @Test
    @DisplayName("When Search API finds an asset, return it")
    void shouldReturnAssetWhenAlreadyExists() {
        AssetResponse existing = AssetResponse.builder()
                .assetName("existing-asset")
                .assetType("APPLICATION")
                .environment("DEV")
                .build();

        when(assetClient.searchAsset("existing-asset")).thenReturn(existing);
        when(assetRepository.findByAssetName("existing-asset")).thenReturn(Optional.empty());

        AssetResponse result = assetService.processAsset("existing-asset", "APPLICATION", "DEV");

        assertThat(result).isEqualTo(existing);
    }

    @Test
    @DisplayName("When Search API does not find asset, create it")
    void shouldCreateAssetWhenNotFound() {
        AssetRequest request = AssetRequest.builder()
                .assetName("new-asset")
                .assetType("APPLICATION")
                .environment("DEV")
                .build();

        AssetResponse created = AssetResponse.builder()
                .assetName("new-asset")
                .assetType("APPLICATION")
                .environment("DEV")
                .build();

        when(assetClient.searchAsset("new-asset")).thenThrow(new AssetNotFoundException("not found"));
        when(assetClient.createAsset(request)).thenReturn(created);
        when(assetRepository.findByAssetName("new-asset")).thenReturn(Optional.empty());
        when(assetRepository.save(org.mockito.ArgumentMatchers.any(AssetEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AssetResponse result = assetService.processAsset("new-asset", "APPLICATION", "DEV");

        assertThat(result).isEqualTo(created);
    }

    @Test
    @DisplayName("When create API fails, throw AssetCreationException")
    void shouldThrowAssetCreationExceptionWhenCreateFails() {
        AssetRequest request = AssetRequest.builder()
                .assetName("bad-asset")
                .assetType("APPLICATION")
                .environment("DEV")
                .build();

        when(assetClient.searchAsset("bad-asset")).thenThrow(new AssetNotFoundException("not found"));
        when(assetClient.createAsset(request)).thenThrow(new ExternalServiceException("down"));

        assertThatThrownBy(() -> assetService.processAsset("bad-asset", "APPLICATION", "DEV"))
                .isInstanceOf(AssetCreationException.class)
                .hasMessageContaining("Failed to create or retrieve asset");
    }
}
