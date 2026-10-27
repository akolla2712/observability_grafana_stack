package com.poc.observability.metrics.dto;

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

    @NotBlank(message = "assetName is required")
    private String assetName;

    @NotBlank(message = "assetType is required")
    private String assetType;

    @NotBlank(message = "environment is required")
    private String environment;
}
