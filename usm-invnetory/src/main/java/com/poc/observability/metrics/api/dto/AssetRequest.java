package com.poc.observability.metrics.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssetRequest {

    @NotBlank(message = "Asset name is required")
    @JsonProperty("assetName")
    private String assetName;

    @NotBlank(message = "Asset type is required")
    @JsonProperty("assetType")
    private String assetType;

    @NotBlank(message = "Environment is required")
    @JsonProperty("environment")
    private String environment;
}
