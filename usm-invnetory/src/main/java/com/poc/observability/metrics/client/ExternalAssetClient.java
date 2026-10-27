package com.poc.observability.metrics.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ExternalAssetClient {

    public void verifyAsset(String assetName) {
        log.info("External validation for assetName={}", assetName);
    }
}
