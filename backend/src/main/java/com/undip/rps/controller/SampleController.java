package com.undip.rps.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.undip.rps.dto.ApiResponse;
import com.undip.rps.dto.RPSDataDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@RestController
@RequestMapping("/sample")
@RequiredArgsConstructor
@Tag(name = "Sample", description = "Sample RPS data endpoints")
public class SampleController {

    private final ObjectMapper objectMapper;

    @GetMapping
    @Operation(summary = "Get sample RPS data", description = "Returns a sample RPS data for testing")
    public ResponseEntity<ApiResponse<RPSDataDTO>> getSample() {
        try {
            ClassPathResource resource = new ClassPathResource("sample_rps.json");
            if (!resource.exists()) {
                return ResponseEntity.ok(ApiResponse.success(createDefaultSample(), "Sample data (default)"));
            }

            try (InputStream is = resource.getInputStream()) {
                RPSDataDTO sample = objectMapper.readValue(is, RPSDataDTO.class);
                return ResponseEntity.ok(ApiResponse.success(sample, "Sample data loaded"));
            }
        } catch (IOException e) {
            log.error("Failed to load sample: {}", e.getMessage());
            return ResponseEntity.ok(ApiResponse.success(createDefaultSample(), "Sample data (default)"));
        }
    }

    private RPSDataDTO createDefaultSample() {
        return RPSDataDTO.builder()
                .identitas(RPSDataDTO.IdentitasDTO.builder()
                        .kode("TRAO6251")
                        .nama("Praktikum Mekatronika dan Robotika")
                        .sks(3)
                        .semester(6)
                        .status("Mata Kuliah Wajib")
                        .prasyarat("Sistem Kendali, Elektronika Dasar")
                        .build())
                .institusi(RPSDataDTO.InstitusiDTO.builder()
                        .programStudi("Sarjana Terapan Teknologi Rekayasa Otomasi")
                        .fakultas("Sekolah Vokasi")
                        .universitas("Universitas Diponegoro")
                        .build())
                .deskripsi("Mata kuliah ini membahas tentang penerapan praktis konsep mekatronika dan robotika dalam industri modern.")
                .build();
    }
}
