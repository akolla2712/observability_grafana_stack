package com.poc.observability.metrics.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AssetResponse {

    private String status;
    private String message;
    private AssetDetails asset;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AssetDetails {
        private String assetName;
        private String assetType;
        private String environment;
    }
}
