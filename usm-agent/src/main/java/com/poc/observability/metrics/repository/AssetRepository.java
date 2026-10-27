package com.poc.observability.metrics.repository;

import com.poc.observability.metrics.entity.AssetEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AssetRepository extends JpaRepository<AssetEntity, Long> {

    Optional<AssetEntity> findByAssetName(String assetName);
}
