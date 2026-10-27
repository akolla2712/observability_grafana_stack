package com.poc.observability.metrics.exception;

public class AssetCreationException extends RuntimeException {

    public AssetCreationException(String message) {
        super(message);
    }

    public AssetCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}
