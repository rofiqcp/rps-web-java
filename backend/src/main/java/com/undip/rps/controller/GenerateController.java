package com.undip.rps.controller;

import com.undip.rps.dto.ApiResponse;
import com.undip.rps.dto.GenerateRequestDTO;
import com.undip.rps.service.AIGeneratorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/generate")
@RequiredArgsConstructor
@Tag(name = "Generate", description = "AI-powered RPS content generation")
public class GenerateController {

    private final AIGeneratorService aiGeneratorService;

    @PostMapping
    @Operation(summary = "Generate RPS content", description = "Generate RPS content using AI based on type: full, cpl, cpmk, weeklyPlan, references, description")
    public ResponseEntity<ApiResponse<Map<String, Object>>> generate(@RequestBody GenerateRequestDTO request) {
        try {
            log.info("Generate request received - type: {}, course: {}", 
                request.getType(), 
                request.getIdentitas() != null ? request.getIdentitas().getNama() : "unknown");

            // Validate required fields
            if (request.getIdentitas() == null || request.getIdentitas().getNama() == null || request.getIdentitas().getNama().isBlank()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Nama mata kuliah harus diisi"));
            }

            Map<String, Object> result = aiGeneratorService.generate(request);
            
            log.info("Generate completed successfully");
            return ResponseEntity.ok(ApiResponse.success(result, "Berhasil generate konten"));
            
        } catch (Exception e) {
            log.error("Generate failed: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}
