package com.poc.observability.metrics;

import com.poc.observability.metrics.controller.AssetController;
import com.poc.observability.metrics.dto.ApiResponse;
import com.poc.observability.metrics.service.AssetService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class AssetControllerTest {

    @Mock
    private AssetService assetService;

    @InjectMocks
    private AssetController assetController;

    @Test
    @DisplayName("GET /usm-agent should return success response")
    void shouldReturnSuccessResponseWhenAssetIsProcessed() {
        ResponseEntity<ApiResponse> response = assetController.getAsset();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo("SUCCESS");
        assertThat(response.getBody().getMessage()).isEqualTo("Asset processed successfully");
    }
}
