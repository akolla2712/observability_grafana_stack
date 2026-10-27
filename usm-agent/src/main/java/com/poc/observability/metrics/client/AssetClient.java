package com.poc.observability.metrics.client;

import com.poc.observability.metrics.dto.AssetRequest;
import com.poc.observability.metrics.dto.AssetResponse;
import com.poc.observability.metrics.exception.AssetNotFoundException;
import com.poc.observability.metrics.exception.ExternalServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
@Slf4j
public class AssetClient {

    private final RestClient assetRestClient;
    private final RestClient assetCreateRestClient;

    public AssetResponse searchAsset(String assetName) {
        log.info("Searching asset with assetName={}", assetName);
        try {
            AssetResponse response = assetRestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("")
                            .queryParam("assetName", assetName)
                            .build())
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (request, httpResponse) -> {
                        throw new AssetNotFoundException("Asset not found for assetName=" + assetName);
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, (request, httpResponse) -> {
                        throw new ExternalServiceException("External service error while searching asset");
                    })
                    .body(AssetResponse.class);

            log.info("Search API response: {}", response);
            return response;
        } catch (AssetNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to call search API for assetName={}", assetName, e);
            throw new ExternalServiceException("Failed to retrieve asset from external service", e);
        }
    }

    public AssetResponse createAsset(AssetRequest request) {
        log.info("Create API request: {}", request);
        try {
            AssetResponse response = assetCreateRestClient.post()
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (req, httpResponse) -> {
                        throw new ExternalServiceException("Asset creation failed for assetName=" + request.getAssetName());
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, (req, httpResponse) -> {
                        throw new ExternalServiceException("External service error while creating asset");
                    })
                    .body(AssetResponse.class);

            log.info("Create API response: {}", response);
            return response;
        } catch (ExternalServiceException e) {
            log.error("Failed to create asset via external API: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("Failed to call create API for assetName={}", request.getAssetName(), e);
            throw new ExternalServiceException("Failed to create asset in external service", e);
        }
    }
}
