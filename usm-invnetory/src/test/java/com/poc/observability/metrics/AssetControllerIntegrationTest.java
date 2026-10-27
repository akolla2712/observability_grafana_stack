package com.poc.observability.metrics;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poc.observability.metrics.api.dto.AssetRequest;
import com.poc.observability.metrics.api.dto.AssetResponse;
import com.poc.observability.metrics.exception.AssetNotFoundException;
import com.poc.observability.metrics.service.AssetService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AssetControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AssetService assetService;

    @Test
    void shouldCreateAssetSuccessfully() throws Exception {
        AssetRequest request = new AssetRequest("Sample Asset", "APPLICATION", "DEV");
        AssetResponse response = AssetResponse.builder()
                .status("SUCCESS")
                .message("Asset processed successfully")
                .asset(new AssetResponse.AssetDetails("Sample Asset", "APPLICATION", "DEV"))
                .build();

        when(assetService.createAsset(any(AssetRequest.class))).thenReturn(response);

        mockMvc.perform(post("/create-asset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.asset.assetName").value("Sample Asset"));
    }

    @Test
    void shouldSearchAssetSuccessfully() throws Exception {
        AssetResponse response = AssetResponse.builder()
                .status("SUCCESS")
                .message("Asset processed successfully")
                .asset(new AssetResponse.AssetDetails("Sample Asset", "APPLICATION", "DEV"))
                .build();

        when(assetService.searchAsset("Sample Asset")).thenReturn(response);

        mockMvc.perform(get("/search-asset").param("assetName", "Sample Asset"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.asset.assetName").value("Sample Asset"));
    }

    @Test
    void shouldReturnNotFoundWhenAssetMissing() throws Exception {
        when(assetService.searchAsset("Missing Asset"))
                .thenThrow(new AssetNotFoundException("Asset not found for name: Missing Asset"));

        mockMvc.perform(get("/search-asset").param("assetName", "Missing Asset"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("FAILED"));
    }
}
