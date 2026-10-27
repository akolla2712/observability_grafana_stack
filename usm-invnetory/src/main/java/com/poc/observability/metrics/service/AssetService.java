package com.poc.observability.metrics.service;

import com.poc.observability.metrics.api.dto.AssetRequest;
import com.poc.observability.metrics.api.dto.AssetResponse;

public interface AssetService {
    AssetResponse createAsset(AssetRequest request);
    AssetResponse searchAsset(String assetName);
}
