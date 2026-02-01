package com.undip.rps.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO untuk data RPS lengkap
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RPSDataDTO {
    
    private String id;
    private String createdAt;
    private String updatedAt;
    
    private IdentitasDTO identitas;
    private InstitusiDTO institusi;
    private OtoritasDTO otoritas;
    private String deskripsi;
    private String deskripsiSingkat;
    
    private List<CPLDTO> cpl;
    private List<IKDTO> ik;
    private List<CPMKDTO> cpmk;
    private List<MingguDTO> minggu;
    private List<ReferensiDTO> referensi;
    
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
    public static class PersonilDTO {
        private String nama;
        private String nip;
        private String jabatan;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OtoritasDTO {
        private PersonilDTO koordinatorMK;
        private PersonilDTO koordinatorGPM;
        private PersonilDTO ketuaProdi;
        private PersonilDTO dekan;
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
    public static class IKDTO {
        private String kode;
        private String pernyataan;
        @JsonProperty("mapping_cpl")
        private String mappingCpl;
        @JsonProperty("mapping_cpmk")
        private String mappingCpmk;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CPMKDTO {
        private String kode;
        private String pernyataan;
        @JsonProperty("mapping_cpl")
        private List<String> mappingCpl;
        
        private Integer n1;
        private Integer n2;
        private Integer n3;
        private Integer n4;
        private Integer n5;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MingguDTO {
        @JsonProperty("mingguKe")
        private Integer mingguKe;
        @JsonProperty("kemampuanAkhir")
        private String kemampuanAkhir;
        @JsonProperty("bahanKajian")
        private String bahanKajian;
        @JsonProperty("metodePembelajaran")
        private MetodePembelajaranDTO metodePembelajaran;
        private String waktu;
        @JsonProperty("pengalamanBelajar")
        private String pengalamanBelajar;
        private PenilaianDTO penilaian;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MetodePembelajaranDTO {
        private String metode;
        private String deskripsi;
        private String aktivitas;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PenilaianDTO {
        private String kriteria;
        private Integer bobot;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReferensiDTO {
        private Integer nomor;
        private String judul;
        private String penulis;
        private Integer tahun;
        private String penerbit;
        private String kota;
        private String isbn;
        private String jenis;
    }
}
