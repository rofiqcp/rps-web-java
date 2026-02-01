package com.undip.rps.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import com.undip.rps.dto.GenerateRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class AIGeneratorService {

    private final OpenAiService openAiService;
    private final ObjectMapper objectMapper;

    @Value("${openai.model:gpt-4o-mini}")
    private String model;

    /**
     * Generate RPS content based on type
     */
    public Map<String, Object> generate(GenerateRequestDTO request) {
        String type = request.getType() != null ? request.getType() : "full";
        
        return switch (type) {
            case "cpl" -> generateCPL(request);
            case "cpmk" -> generateCPMK(request);
            case "weeklyPlan" -> generateWeeklyPlan(request);
            case "references" -> generateReferences(request);
            case "description" -> generateDescription(request);
            default -> generateFull(request);
        };
    }

    private Map<String, Object> generateFull(GenerateRequestDTO request) {
        String prompt = buildFullPrompt(request);
        String response = callOpenAI(prompt);
        return parseJsonResponse(response);
    }

    private Map<String, Object> generateCPL(GenerateRequestDTO request) {
        String prompt = buildCPLPrompt(request);
        String response = callOpenAI(prompt);
        Map<String, Object> parsed = parseJsonResponse(response);
        
        Map<String, Object> result = new HashMap<>();
        result.put("cpl", parsed.get("cpl"));
        if (parsed.containsKey("ik")) {
            result.put("ik", parsed.get("ik"));
        }
        return result;
    }

    private Map<String, Object> generateCPMK(GenerateRequestDTO request) {
        String prompt = buildCPMKPrompt(request);
        String response = callOpenAI(prompt);
        Map<String, Object> parsed = parseJsonResponse(response);
        
        Map<String, Object> result = new HashMap<>();
        result.put("cpmk", parsed.get("cpmk"));
        if (parsed.containsKey("ik")) {
            result.put("ik", parsed.get("ik"));
        }
        return result;
    }

    private Map<String, Object> generateWeeklyPlan(GenerateRequestDTO request) {
        String prompt = buildWeeklyPlanPrompt(request);
        String response = callOpenAI(prompt);
        Map<String, Object> parsed = parseJsonResponse(response);
        
        Map<String, Object> result = new HashMap<>();
        result.put("minggu", parsed.get("minggu"));
        return result;
    }

    private Map<String, Object> generateReferences(GenerateRequestDTO request) {
        String prompt = buildReferencesPrompt(request);
        String response = callOpenAI(prompt);
        Map<String, Object> parsed = parseJsonResponse(response);
        
        Map<String, Object> result = new HashMap<>();
        result.put("referensi", parsed.get("referensi"));
        return result;
    }

    private Map<String, Object> generateDescription(GenerateRequestDTO request) {
        String prompt = buildDescriptionPrompt(request);
        String response = callOpenAI(prompt);
        Map<String, Object> parsed = parseJsonResponse(response);
        
        Map<String, Object> result = new HashMap<>();
        result.put("deskripsi", parsed.get("deskripsi"));
        return result;
    }

    private String callOpenAI(String prompt) {
        log.info("Calling OpenAI with model: {}", model);
        
        List<ChatMessage> messages = new ArrayList<>();
        messages.add(new ChatMessage("system", "Anda adalah ahli kurikulum pendidikan tinggi Indonesia. Selalu output dalam format JSON murni tanpa markdown code block."));
        messages.add(new ChatMessage("user", prompt));

        ChatCompletionRequest completionRequest = ChatCompletionRequest.builder()
                .model(model)
                .messages(messages)
                .temperature(0.7)
                .maxTokens(4000)
                .build();

        var completion = openAiService.createChatCompletion(completionRequest);
        String content = completion.getChoices().get(0).getMessage().getContent();
        
        log.debug("OpenAI response: {}", content.substring(0, Math.min(500, content.length())));
        return content;
    }

    private Map<String, Object> parseJsonResponse(String response) {
        String text = response.trim();
        
        // Handle markdown code blocks
        if (text.startsWith("```")) {
            String[] lines = text.split("\n");
            StringBuilder sb = new StringBuilder();
            boolean inBlock = false;
            for (String line : lines) {
                if (line.startsWith("```")) {
                    inBlock = !inBlock;
                    continue;
                }
                if (inBlock || !line.startsWith("```")) {
                    sb.append(line).append("\n");
                }
            }
            text = sb.toString().trim();
        }

        // Remove trailing commas
        text = text.replaceAll(",\\s*([}\\]])", "$1");

        try {
            return objectMapper.readValue(text, Map.class);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse JSON response: {}", e.getMessage());
            
            // Try to extract JSON object
            Pattern pattern = Pattern.compile("\\{[\\s\\S]*\\}");
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                try {
                    return objectMapper.readValue(matcher.group(), Map.class);
                } catch (JsonProcessingException ex) {
                    log.error("Failed to parse extracted JSON: {}", ex.getMessage());
                }
            }
            
            throw new RuntimeException("Failed to parse AI response as JSON: " + e.getMessage());
        }
    }

    private String buildFullPrompt(GenerateRequestDTO request) {
        var identitas = request.getIdentitas();
        String courseName = identitas != null ? identitas.getNama() : "Mata Kuliah";
        String courseCode = identitas != null && identitas.getKode() != null ? identitas.getKode() : "MK001";
        int sks = identitas != null && identitas.getSks() != null ? identitas.getSks() : 3;
        int semester = identitas != null && identitas.getSemester() != null ? identitas.getSemester() : 1;
        String status = identitas != null && identitas.getStatus() != null ? identitas.getStatus() : "Mata Kuliah Wajib";
        String prereq = identitas != null && identitas.getPrasyarat() != null ? identitas.getPrasyarat() : "-";
        String context = request.getAdditionalContext() != null ? request.getAdditionalContext() : "";

        return String.format("""
Anda adalah ahli kurikulum pendidikan tinggi Indonesia. Buatkan Rencana Pembelajaran Semester (RPS) lengkap untuk mata kuliah berikut:

## Informasi Mata Kuliah:
- Nama: %s
- Kode: %s
- SKS: %d
- Semester: %d
- Status: %s
- Prasyarat: %s
- Konteks tambahan:
%s

## Instruksi:
Buatkan RPS dalam format JSON dengan struktur PERSIS seperti berikut. PENTING: Hanya output JSON murni tanpa markdown code block.

{
  "deskripsi": "Deskripsi mata kuliah 3-5 kalimat yang menjelaskan tujuan, cakupan, dan manfaat mata kuliah ini bagi mahasiswa",
  
  "cpl": [
    {"kode": "CPL 1", "pernyataan": "Capaian Pembelajaran Lulusan yang relevan dengan mata kuliah"}
  ],
  
  "ik": [
    {"kode": "IK 1-1", "pernyataan": "Indikator kinerja spesifik untuk CPL 1 dan CPMK 1", "mapping_cpl": "CPL 1", "mapping_cpmk": "CPMK 1"}
  ],

  "cpmk": [
    {"kode": "CPMK 1", "pernyataan": "Mahasiswa mampu [capaian spesifik]", "mapping_cpl": "CPL1", "N1": 5, "N2": 5, "N3": 5, "N4": 5, "N5": 0, "N_cpmk": 30}
  ],

  "minggu": [
    {"mingguKe": 1, "kemampuanAkhir": "CPMK 1", "bahanKajian": "Topik minggu 1", "metodePembelajaran": {"metode": "TM SCL", "deskripsi": "peran dosen dalam metode pembelajaran", "aktivitas": "aktivitas mahasiswa dalam metode pembelajaran"}, "waktu": "TM Ceramah 1x50', Kuis, Tugas Mandiri", "pengalamanBelajar": "pengalaman belajar mahasiswa", "penilaian": {"kriteria": "kriteria indikator pencapaian", "bobot": 5}},
    {"mingguKe": 8, "kemampuanAkhir": "UTS", "bahanKajian": "Ujian Tengah Semester (UTS)", "metodePembelajaran": {"metode": "Ujian", "deskripsi": "Ujian untuk mengukur pemahaman terhadap materi minggu 1-7", "aktivitas": "Mengerjakan soal ujian"}, "waktu": "3x50'", "pengalamanBelajar": "UTS", "penilaian": {"kriteria": "UTS", "bobot": 20}},
    {"mingguKe": 16, "kemampuanAkhir": "UAS", "bahanKajian": "Ujian Akhir Semester (UAS)", "metodePembelajaran": {"metode": "Ujian", "deskripsi": "Ujian akhir semester dengan demo proyek dan presentasi hasil pembelajaran", "aktivitas": "Demo proyek dan presentasi hasil pembelajaran"}, "waktu": "3x50'", "pengalamanBelajar": "UAS", "penilaian": {"kriteria": "UAS", "bobot": 20}}
  ],
    
  "referensi": [
    {"nomor": 1, "judul": "Judul Buku", "penulis": "Nama Penulis", "tahun": 2023, "penerbit": "Nama Penerbit", "kota": "Jakarta", "isbn": "", "jenis": "Utama"}
  ]
}

## Catatan Penting:
- Gunakan bahasa Indonesia yang baik dan akademis
- Konten harus relevan dengan "%s"
- Pastikan semua 16 minggu terisi lengkap (14 pertemuan + UTS minggu 8 + UAS minggu 16)
- Minggu 8 = UTS, Minggu 16 = UAS (hanya ada field mingguKe, kemampuanAkhir, bahanKajian, metodePembelajaran, waktu, pengalamanBelajar, penilaian)
- N1(Partisipatif 20%%), N2(Project/Problem/Case Based Learning 30%%), N3(Kuis 10%%), N4(UTS 20%%), N5(UAS 20%%)
- Total N1 dari semua CPMK harus 20%%, N2-N5 juga sama sesuai proporsi di atas
- Total bobot penilaian = 100%% dari N_cpmk semua CPMK
- Format kode CPL (CPL 1, CPL 2), CPMK (CPMK 1, CPMK 2), IK (IK 1-1, IK 2-1)
- Mapping: CPMK memetakan ke CPL, IK memetakan ke CPMK
- Setiap minggu (selain UTS/UAS) harus ada semua field lengkap
- Total bobot dari semua minggu (14 pertemuan) harus 100%%
- WAJIB untuk setiap minggu (selain UTS/UAS):
   - "metodePembelajaran.metode": pilih 1 dari: TM SCL, CBL (Case Based Learning), PBL (Problem Based Learning), PjBL (Project Based Learning)
   - "metodePembelajaran.deskripsi": minimal 15 kata menjelaskan peran dosen dalam metode pembelajaran
   - "metodePembelajaran.aktivitas": minimal 15 kata menjelaskan aktivitas mahasiswa
   - "pengalamanBelajar": minimal 25 kata pengalaman belajar yang didapat mahasiswa
   - "penilaian.kriteria": minimal 20 kata kriteria penilaian yang jelas
   - "penilaian.bobot": setiap bobot dari beberapa minggu untuk satu CPMK dijumlahkan harus sesuai N_cpmk
   - "bahanKajian": sesuai dengan course_name dan relevan dengan CPMK yang dituju, spesifik dan bervariasi setiap minggu
   - "waktu": untuk mata kuliah teori gunakan format "TM Ceramah %dx50', Kuis, Tugas Mandiri" atau variasi CBL/PBL/PjBL sesuai metode
- Untuk mata kuliah Praktikum, variasikan metode pembelajaran dan gunakan format waktu "Praktikum %dx170', Hands-on, Laporan"
- Referensi harus mencakup: 1 buku internasional, 2-3 buku nasional, 2 jurnal internasional, 2 jurnal nasional
- Output JSON saja, tanpa markdown formatting atau penjelasan.
""", courseName, courseCode, sks, semester, status, prereq, context, courseName);
    }

    private String buildCPLPrompt(GenerateRequestDTO request) {
        var identitas = request.getIdentitas();
        String courseName = identitas != null ? identitas.getNama() : "Mata Kuliah";
        String context = request.getAdditionalContext() != null ? request.getAdditionalContext() : "";

        return String.format("""
Generate CPL (Capaian Pembelajaran Lulusan) untuk mata kuliah: %s

Konteks: %s

Output format JSON:
{
  "cpl": [
    {"kode": "CPL 1", "pernyataan": "Mampu [capaian]"},
    {"kode": "CPL 2", "pernyataan": "Mampu [capaian]"}
  ],
  "ik": [
    {"kode": "IK 1", "pernyataan": "Indikator untuk CPL 1", "mapping_cpl": "CPL 1", "mapping_cpmk": ""}
  ]
}

Buat 3-5 CPL yang relevan. Output JSON saja.
""", courseName, context);
    }

    private String buildCPMKPrompt(GenerateRequestDTO request) {
        var identitas = request.getIdentitas();
        String courseName = identitas != null ? identitas.getNama() : "Mata Kuliah";
        String context = request.getAdditionalContext() != null ? request.getAdditionalContext() : "";
        String deskripsi = request.getDeskripsi() != null ? request.getDeskripsi() : "";
        
        StringBuilder cplInfo = new StringBuilder();
        if (request.getCpl() != null) {
            for (var cpl : request.getCpl()) {
                cplInfo.append(String.format("- %s: %s\n", cpl.getKode(), cpl.getPernyataan()));
            }
        }

        return String.format("""
Generate CPMK (Capaian Pembelajaran Mata Kuliah) untuk:
- Mata Kuliah: %s
- Deskripsi: %s

CPL yang tersedia:
%s

Konteks: %s

Output format JSON:
{
  "cpmk": [
    {"kode": "CPMK 1", "pernyataan": "Mahasiswa mampu [capaian]", "mapping_cpl": ["CPL 1", "CPL 2"], "n1": 5, "n2": 8, "n3": 2, "n4": 5, "n5": 5}
  ],
  "ik": [
    {"kode": "IK 1", "pernyataan": "Indikator", "mapping_cpl": "CPL 1", "mapping_cpmk": "CPMK 1"}
  ]
}

n1=Tugas, n2=Kuis, n3=UTS, n4=UAS, n5=Praktikum
Buat 3-6 CPMK. Output JSON saja.
""", courseName, deskripsi, cplInfo, context);
    }

    private String buildWeeklyPlanPrompt(GenerateRequestDTO request) {
        var identitas = request.getIdentitas();
        String courseName = identitas != null ? identitas.getNama() : "Mata Kuliah";
        int sks = identitas != null && identitas.getSks() != null ? identitas.getSks() : 3;
        String context = request.getAdditionalContext() != null ? request.getAdditionalContext() : "";
        
        StringBuilder cpmkInfo = new StringBuilder();
        if (request.getCpmk() != null) {
            for (var cpmk : request.getCpmk()) {
                cpmkInfo.append(String.format("- %s: %s\n", 
                    cpmk.getKode(), cpmk.getPernyataan()));
            }
        }

        return String.format("""
Anda adalah ahli kurikulum pendidikan tinggi Indonesia.

Generate Rencana Pembelajaran Mingguan untuk:
- Mata Kuliah: %s
- SKS: %d

CPMK:
%s

Konteks: %s

Output format JSON dengan 16 minggu (EXACT field names):
[
  {"mingguKe": 1, "kemampuanAkhir": "CPMK 1", "bahanKajian": "Pengenalan dan konsep dasar", "metodePembelajaran": {"metode": "Ceramah", "deskripsi": "Penyampaian konsep fundamental melalui presentasi interaktif dengan melibatkan mahasiswa dalam diskusi materi", "aktivitas": "Mendengarkan penjelasan konsep dasar dan diskusi mendalam tentang prinsip fundamental mata kuliah"}, "waktu": "%dx50'", "pengalamanBelajar": "Memahami terminologi dasar mengingat definisi konsep fundamental mengikuti presentasi diskusi kelas", "penilaian": {"kriteria": "Pemahaman konsep dasar ketepatan definisi keterlibatan dalam diskusi kelas", "bobot": 5}},
  {"mingguKe": 8, "kemampuanAkhir": "UTS", "bahanKajian": "Ujian Tengah Semester (UTS)", "metodePembelajaran": {"metode": "Ujian", "deskripsi": "Penilaian tertulis komprehensif mencakup seluruh materi minggu 1-7 untuk mengukur kompetensi dasar", "aktivitas": "Pelaksanaan ujian tulis sesuai jadwal akademik dan evaluasi penguasaan materi"}, "waktu": "%dx50'", "pengalamanBelajar": "UTS", "penilaian": {"kriteria": "Ketepatan jawaban pemahaman konsep penguasaan materi dan kedalaman analisis", "bobot": 20}},
  {"mingguKe": 16, "kemampuanAkhir": "UAS", "bahanKajian": "Ujian Akhir Semester: demo proyek dan presentasi hasil pembelajaran", "metodePembelajaran": {"metode": "Ujian", "deskripsi": "Penilaian akhir semester melalui demo proyek integrasi dan presentasi hasil pembelajaran keseluruhan", "aktivitas": "Pelaksanaan ujian akhir semester termasuk demo proyek dan presentasi hasil pembelajaran akhir"}, "waktu": "%dx50'", "pengalamanBelajar": "UAS", "penilaian": {"kriteria": "Kualitas proyek demo presentasi integrasi dan pemahaman holistik", "bobot": 20}}
]

Catatan Penting:
- WAJIB setiap minggu (selain UTS/UAS):
  - "metodePembelajaran.metode": pilih 1 dari: Ceramah, Diskusi, Praktikum, Kuis, Tugas, PBL, CBL, PjBL
  - "metodePembelajaran.deskripsi": HARUS 20+ kata penjelasan metode pembelajaran
  - "metodePembelajaran.aktivitas": HARUS 20+ kata penjelasan aktivitas mahasiswa
  - "pengalamanBelajar": HARUS 20+ kata pengalaman belajar mahasiswa
  - "penilaian.kriteria": HARUS 20+ kata kriteria indikator pencapaian
- Total bobot semua minggu (14 pertemuan) = 100%%
- Minggu 8 = UTS (bobot 20%%), Minggu 16 = UAS (bobot 20%%)
- Distribusi bobot 14 pertemuan + 2 ujian = 100%%
- Konten relevan dengan "%s"
- Output JSON PURE tanpa markdown
""", courseName, sks, cpmkInfo, context, sks, sks, sks, courseName);
    }

    private String buildReferencesPrompt(GenerateRequestDTO request) {
        var identitas = request.getIdentitas();
        String courseName = identitas != null ? identitas.getNama() : "Mata Kuliah";
        String context = request.getAdditionalContext() != null ? request.getAdditionalContext() : "";

        return String.format("""
Generate referensi/pustaka untuk mata kuliah: %s

Konteks: %s

Output format JSON:
{
  "referensi": [
    {"nomor": 1, "judul": "Judul Buku", "penulis": "Nama Penulis", "tahun": 2023, "penerbit": "Nama Penerbit", "kota": "Jakarta", "isbn": "", "jenis": "Utama"},
    {"nomor": 2, "judul": "Judul Jurnal", "penulis": "Nama Penulis", "tahun": 2022, "penerbit": "Nama Jurnal", "kota": "", "isbn": "", "jenis": "Pendukung"}
  ]
}

Sertakan:
- 1-2 Buku internasional (Utama)
- 2-3 Buku nasional (Utama/Pendukung)
- 2-3 Jurnal (Pendukung)

Output JSON saja.
""", courseName, context);
    }

    private String buildDescriptionPrompt(GenerateRequestDTO request) {
        var identitas = request.getIdentitas();
        String courseName = identitas != null ? identitas.getNama() : "Mata Kuliah";
        String context = request.getAdditionalContext() != null ? request.getAdditionalContext() : "";

        return String.format("""
Generate deskripsi mata kuliah untuk: %s

Konteks: %s

Output format JSON:
{
  "deskripsi": "Deskripsi mata kuliah 3-5 kalimat yang menjelaskan tujuan, cakupan materi, dan manfaat bagi mahasiswa."
}

Output JSON saja.
""", courseName, context);
    }
}
