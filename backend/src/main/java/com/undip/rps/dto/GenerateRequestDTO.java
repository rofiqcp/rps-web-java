package com.undip.rps.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenerateRequestDTO {
    
    private String type; // full, cpl, cpmk, weeklyPlan, references, description
    private IdentitasDTO identitas;
    private InstitusiDTO institusi;
    private String jenisMK; // teori, praktikum, campuran
    private String additionalContext;
    private String deskripsi;
    private List<CPLDTO> cpl;
    private List<CPMKDTO> cpmk;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IdentitasDTO {
        private String kode;
        private String nama;
        private Integer sks;
        private Integer semester;
        private String status;
        private String prasyarat;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InstitusiDTO {
        private String programStudi;
        private String fakultas;
        private String universitas;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CPLDTO {
        private String kode;
        private String pernyataan;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CPMKDTO {
        private String kode;
        private String pernyataan;
        private String mappingCpl;
    }
}
