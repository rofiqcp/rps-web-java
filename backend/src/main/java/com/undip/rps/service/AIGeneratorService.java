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
Buatkan Rencana Pembelajaran Semester (RPS) lengkap untuk mata kuliah berikut:

## Informasi Mata Kuliah:
- Nama: %s
- Kode: %s
- SKS: %d
- Semester: %d
- Status: %s
- Prasyarat: %s

## Konteks Tambahan:
%s

## Instruksi:
Buatkan RPS dalam format JSON dengan struktur berikut:

{
  "deskripsi": "Deskripsi mata kuliah 3-5 kalimat",
  "cpl": [{"kode": "CPL 1", "pernyataan": "Capaian Pembelajaran Lulusan"}],
  "ik": [{"kode": "IK 1", "pernyataan": "Indikator kinerja", "mapping_cpl": "CPL 1", "mapping_cpmk": "CPMK 1"}],
  "cpmk": [{"kode": "CPMK 1", "pernyataan": "Capaian spesifik", "mapping_cpl": ["CPL 1", "CPL 2"], "n1": 5, "n2": 5, "n3": 5, "n4": 5, "n5": 0}],
  "minggu": [
    {"minggu": 1, "mapping_cpmk": "CPMK 1", "materi": "Topik", "kemampuan_akhir": "...", "indikator": "...", "metode": "TM SCL", "waktu": 150, "pengalaman_belajar": "...", "penilaian": "...", "bobot": 7, "referensi": "1, 2"},
    {"minggu": 8, "mapping_cpmk": "", "materi": "Ujian Tengah Semester (UTS)", "kemampuan_akhir": "", "indikator": "", "metode": "", "waktu": 0, "pengalaman_belajar": "", "penilaian": "", "bobot": 0, "referensi": ""},
    {"minggu": 16, "mapping_cpmk": "", "materi": "Ujian Akhir Semester (UAS)", "kemampuan_akhir": "", "indikator": "", "metode": "", "waktu": 0, "pengalaman_belajar": "", "penilaian": "", "bobot": 0, "referensi": ""}
  ],
  "referensi": [{"nomor": 1, "judul": "Judul Buku", "penulis": "Nama Penulis", "tahun": 2023, "penerbit": "Penerbit", "kota": "Jakarta", "isbn": "", "jenis": "Utama"}]
}

## Catatan:
- Gunakan bahasa Indonesia yang baik dan akademis
- 16 minggu (14 pertemuan + UTS minggu 8 + UAS minggu 16)
- Total bobot dari semua minggu = 100%%
- Metode: TM SCL, CBL, PBL, PjBL
- Output JSON saja tanpa markdown
""", courseName, courseCode, sks, semester, status, prereq, context);
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
Generate Rencana Pembelajaran Mingguan untuk:
- Mata Kuliah: %s
- SKS: %d

CPMK:
%s

Konteks: %s

Output format JSON dengan 16 minggu:
{
  "minggu": [
    {
      "minggu": 1,
      "mapping_cpmk": "CPMK 1",
      "materi": "Pengantar dan dasar-dasar",
      "kemampuan_akhir": "Mahasiswa mampu memahami konsep dasar",
      "indikator": "Dapat menjelaskan konsep dasar",
      "metode": "TM SCL",
      "waktu": %d,
      "pengalaman_belajar": "Mahasiswa mempelajari konsep dasar melalui kuliah dan diskusi",
      "penilaian": "Tugas dan kuis",
      "bobot": 7,
      "referensi": "1, 2"
    },
    {"minggu": 8, "mapping_cpmk": "", "materi": "Ujian Tengah Semester (UTS)", "kemampuan_akhir": "", "indikator": "", "metode": "", "waktu": 0, "pengalaman_belajar": "", "penilaian": "", "bobot": 0, "referensi": ""},
    {"minggu": 16, "mapping_cpmk": "", "materi": "Ujian Akhir Semester (UAS)", "kemampuan_akhir": "", "indikator": "", "metode": "", "waktu": 0, "pengalaman_belajar": "", "penilaian": "", "bobot": 0, "referensi": ""}
  ]
}

Metode: TM SCL, CBL, PBL, PjBL
Total bobot = 100%%. Output JSON saja.
""", courseName, sks, cpmkInfo, context, sks * 50);
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
