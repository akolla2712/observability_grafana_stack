package com.poc.observability.metrics.service;

import com.poc.observability.metrics.dto.AssetRequest;
import com.poc.observability.metrics.dto.AssetResponse;

public interface AssetService {

    AssetResponse processAsset(String assetName, String assetType, String environment);

    AssetResponse getAssetByName(String assetName);

    AssetResponse createAsset(AssetRequest assetRequest);
}
