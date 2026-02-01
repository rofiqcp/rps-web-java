package com.undip.rps.controller;

import com.undip.rps.dto.ExportRequestDTO;
import com.undip.rps.service.DocxExportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@RestController
@RequestMapping("/export")
@RequiredArgsConstructor
@Tag(name = "Export", description = "Export RPS to DOCX")
public class ExportController {

    private final DocxExportService docxExportService;

    @PostMapping
    @Operation(summary = "Export RPS to DOCX", description = "Export RPS data to Microsoft Word DOCX format")
    public ResponseEntity<byte[]> exportToDocx(@RequestBody ExportRequestDTO request) {
        try {
            log.info("Export request received");

            if (request.getRpsData() == null) {
                return ResponseEntity.badRequest().build();
            }

            byte[] docxBytes = docxExportService.exportToDocx(request.getRpsData());

            String filename = "RPS";
            if (request.getRpsData().getIdentitas() != null) {
                String kode = request.getRpsData().getIdentitas().getKode();
                String nama = request.getRpsData().getIdentitas().getNama();
                if (kode != null && !kode.isBlank()) {
                    filename += "_" + kode;
                }
                if (nama != null && !nama.isBlank()) {
                    filename += "_" + nama.replaceAll("\\s+", "_");
                }
            }
            filename += ".docx";

            // Encode filename for Content-Disposition header
            String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8).replace("+", "%20");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.wordprocessingml.document"));
            headers.setContentDispositionFormData("attachment", encodedFilename);
            headers.set("Content-Disposition", "attachment; filename=\"" + encodedFilename + "\"; filename*=UTF-8''" + encodedFilename);

            log.info("Export completed successfully - {} bytes", docxBytes.length);
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(docxBytes);

        } catch (Exception e) {
            log.error("Export failed: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
