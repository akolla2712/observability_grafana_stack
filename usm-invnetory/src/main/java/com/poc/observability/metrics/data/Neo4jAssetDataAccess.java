package com.poc.observability.metrics.data;

import com.poc.observability.metrics.domain.entity.Asset;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.SessionConfig;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class Neo4jAssetDataAccess {

    private final Driver driver;

    public Asset createAsset(Asset asset) {
        log.info("Executing Neo4j query to create asset={}", asset.getName());
        try (Session session = driver.session(SessionConfig.forDatabase("neo4j"))) {
            String query = "CREATE (a:Asset {name: $name, assetType: $assetType, environment: $environment}) RETURN a";
            Result result = session.run(query, org.neo4j.driver.Values.parameters(
                    "name", asset.getName(),
                    "assetType", asset.getAssetType(),
                    "environment", asset.getEnvironment()));

            if (result.hasNext()) {
                var record = result.next();
                var created = record.get("a").asNode();
                asset.setId(created.get("name").asString());
                asset.setName(created.get("name").asString());
                asset.setAssetType(created.get("assetType").asString());
                asset.setEnvironment(created.get("environment").asString());
                return asset;
            }
            throw new IllegalStateException("Asset creation returned no result");
        } catch (Exception ex) {
            log.error("NEO4J_UNAVAILABLE: Failed to create asset={} because Neo4j is unavailable. Error={}", asset.getName(), ex.getMessage(), ex);
            throw ex;
        }
    }

    public Optional<Asset> findByName(String assetName) {
        log.info("Executing Neo4j query to search asset={}", assetName);
        try (Session session = driver.session(SessionConfig.forDatabase("neo4j"))) {
            String query = "MATCH (a) WHERE a.name = $assetName RETURN a";
            Result result = session.run(query, org.neo4j.driver.Values.parameters("assetName", assetName));

            if (result.hasNext()) {
                var record = result.next();
                var node = record.get("a").asNode();
                Asset asset = Asset.builder()
                        .id(node.get("name").asString())
                        .name(node.get("name").asString())
                        .assetType(node.get("assetType").asString())
                        .environment(node.get("environment").asString())
                        .build();
                return Optional.of(asset);
            }
            return Optional.empty();
        } catch (Exception ex) {
            log.error("NEO4J_UNAVAILABLE: Failed to retrieve asset={} because Neo4j is unavailable. Error={}", assetName, ex.getMessage(), ex);
            throw ex;
        }
    }
}
