package com.undip.rps.service;

import com.undip.rps.dto.RPSDataDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

@Slf4j
@Service
public class DocxExportService {

    /**
     * Export RPS data to DOCX format
     */
    public byte[] exportToDocx(RPSDataDTO rpsData) throws IOException {
        try (XWPFDocument document = new XWPFDocument()) {
            // Set page margins
            CTSectPr sectPr = document.getDocument().getBody().addNewSectPr();
            CTPageMar pageMar = sectPr.addNewPgMar();
            pageMar.setLeft(BigInteger.valueOf(1440)); // 1 inch
            pageMar.setRight(BigInteger.valueOf(1440));
            pageMar.setTop(BigInteger.valueOf(1440));
            pageMar.setBottom(BigInteger.valueOf(1440));

            // Title
            addTitle(document, "RENCANA PEMBELAJARAN SEMESTER (RPS)");
            addEmptyLine(document);

            // Identitas Mata Kuliah
            addSectionTitle(document, "A. IDENTITAS MATA KULIAH");
            addIdentitasTable(document, rpsData);
            addEmptyLine(document);

            // Otoritas
            addSectionTitle(document, "B. OTORITAS");
            addOtoritasTable(document, rpsData);
            addEmptyLine(document);

            // Deskripsi
            addSectionTitle(document, "C. DESKRIPSI MATA KULIAH");
            addParagraph(document, rpsData.getDeskripsi() != null ? rpsData.getDeskripsi() : "");
            addEmptyLine(document);

            // CPL
            addSectionTitle(document, "D. CAPAIAN PEMBELAJARAN LULUSAN (CPL)");
            addCPLTable(document, rpsData.getCpl());
            addEmptyLine(document);

            // CPMK
            addSectionTitle(document, "E. CAPAIAN PEMBELAJARAN MATA KULIAH (CPMK)");
            addCPMKTable(document, rpsData.getCpmk());
            addEmptyLine(document);

            // IK
            addSectionTitle(document, "F. INDIKATOR KINERJA (IK)");
            addIKTable(document, rpsData.getIk());
            addEmptyLine(document);

            // Penilaian
            addSectionTitle(document, "G. KOMPONEN PENILAIAN");
            addPenilaianTable(document, rpsData.getCpmk());
            addEmptyLine(document);

            // Rencana Mingguan
            addSectionTitle(document, "H. RENCANA PEMBELAJARAN MINGGUAN");
            addWeeklyPlanTable(document, rpsData.getMinggu());
            addEmptyLine(document);

            // Referensi
            addSectionTitle(document, "I. REFERENSI");
            addReferensiList(document, rpsData.getReferensi());

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            document.write(out);
            return out.toByteArray();
        }
    }

    private void addTitle(XWPFDocument document, String text) {
        XWPFParagraph paragraph = document.createParagraph();
        paragraph.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun run = paragraph.createRun();
        run.setText(text);
        run.setBold(true);
        run.setFontSize(16);
        run.setFontFamily("Times New Roman");
    }

    private void addSectionTitle(XWPFDocument document, String text) {
        XWPFParagraph paragraph = document.createParagraph();
        XWPFRun run = paragraph.createRun();
        run.setText(text);
        run.setBold(true);
        run.setFontSize(12);
        run.setFontFamily("Times New Roman");
    }

    private void addParagraph(XWPFDocument document, String text) {
        XWPFParagraph paragraph = document.createParagraph();
        paragraph.setAlignment(ParagraphAlignment.BOTH);
        XWPFRun run = paragraph.createRun();
        run.setText(text);
        run.setFontSize(11);
        run.setFontFamily("Times New Roman");
    }

    private void addEmptyLine(XWPFDocument document) {
        document.createParagraph();
    }

    private void addIdentitasTable(XWPFDocument document, RPSDataDTO rpsData) {
        XWPFTable table = document.createTable(7, 2);
        setTableWidth(table, 9000);

        var identitas = rpsData.getIdentitas();
        var institusi = rpsData.getInstitusi();

        String[][] data = {
            {"Nama Mata Kuliah", identitas != null ? identitas.getNama() : ""},
            {"Kode Mata Kuliah", identitas != null ? identitas.getKode() : ""},
            {"SKS", identitas != null && identitas.getSks() != null ? String.valueOf(identitas.getSks()) : ""},
            {"Semester", identitas != null && identitas.getSemester() != null ? String.valueOf(identitas.getSemester()) : ""},
            {"Status", identitas != null ? identitas.getStatus() : ""},
            {"Prasyarat", identitas != null ? identitas.getPrasyarat() : ""},
            {"Program Studi", institusi != null ? institusi.getProgramStudi() : ""}
        };

        for (int i = 0; i < data.length; i++) {
            setCellText(table.getRow(i).getCell(0), data[i][0], true);
            setCellText(table.getRow(i).getCell(1), data[i][1], false);
        }
    }

    private void addOtoritasTable(XWPFDocument document, RPSDataDTO rpsData) {
        XWPFTable table = document.createTable(4, 3);
        setTableWidth(table, 9000);

        // Header
        setCellText(table.getRow(0).getCell(0), "Jabatan", true);
        setCellText(table.getRow(0).getCell(1), "Nama", true);
        setCellText(table.getRow(0).getCell(2), "NIP", true);

        var otoritas = rpsData.getOtoritas();
        if (otoritas != null) {
            String[][] data = {
                {"Koordinator MK", 
                    otoritas.getKoordinatorMK() != null ? otoritas.getKoordinatorMK().getNama() : "",
                    otoritas.getKoordinatorMK() != null ? otoritas.getKoordinatorMK().getNip() : ""},
                {"Koordinator GPM",
                    otoritas.getKoordinatorGPM() != null ? otoritas.getKoordinatorGPM().getNama() : "",
                    otoritas.getKoordinatorGPM() != null ? otoritas.getKoordinatorGPM().getNip() : ""},
                {"Ketua Prodi",
                    otoritas.getKetuaProdi() != null ? otoritas.getKetuaProdi().getNama() : "",
                    otoritas.getKetuaProdi() != null ? otoritas.getKetuaProdi().getNip() : ""}
            };

            for (int i = 0; i < data.length; i++) {
                setCellText(table.getRow(i + 1).getCell(0), data[i][0], false);
                setCellText(table.getRow(i + 1).getCell(1), data[i][1], false);
                setCellText(table.getRow(i + 1).getCell(2), data[i][2], false);
            }
        }
    }

    private void addCPLTable(XWPFDocument document, List<RPSDataDTO.CPLDTO> cplList) {
        if (cplList == null || cplList.isEmpty()) return;

        XWPFTable table = document.createTable(cplList.size() + 1, 2);
        setTableWidth(table, 9000);

        // Header
        setCellText(table.getRow(0).getCell(0), "Kode", true);
        setCellText(table.getRow(0).getCell(1), "Pernyataan CPL", true);

        for (int i = 0; i < cplList.size(); i++) {
            var cpl = cplList.get(i);
            setCellText(table.getRow(i + 1).getCell(0), cpl.getKode(), false);
            setCellText(table.getRow(i + 1).getCell(1), cpl.getPernyataan(), false);
        }
    }

    private void addCPMKTable(XWPFDocument document, List<RPSDataDTO.CPMKDTO> cpmkList) {
        if (cpmkList == null || cpmkList.isEmpty()) return;

        XWPFTable table = document.createTable(cpmkList.size() + 1, 3);
        setTableWidth(table, 9000);

        // Header
        setCellText(table.getRow(0).getCell(0), "Kode", true);
        setCellText(table.getRow(0).getCell(1), "Pernyataan CPMK", true);
        setCellText(table.getRow(0).getCell(2), "Mapping CPL", true);

        for (int i = 0; i < cpmkList.size(); i++) {
            var cpmk = cpmkList.get(i);
            setCellText(table.getRow(i + 1).getCell(0), cpmk.getKode(), false);
            setCellText(table.getRow(i + 1).getCell(1), cpmk.getPernyataan(), false);
            String mappingCpl = cpmk.getMappingCpl() != null ? String.join(", ", cpmk.getMappingCpl()) : "";
            setCellText(table.getRow(i + 1).getCell(2), mappingCpl, false);
        }
    }

    private void addIKTable(XWPFDocument document, List<RPSDataDTO.IKDTO> ikList) {
        if (ikList == null || ikList.isEmpty()) return;

        XWPFTable table = document.createTable(ikList.size() + 1, 4);
        setTableWidth(table, 9000);

        // Header
        setCellText(table.getRow(0).getCell(0), "Kode IK", true);
        setCellText(table.getRow(0).getCell(1), "Pernyataan IK", true);
        setCellText(table.getRow(0).getCell(2), "CPL", true);
        setCellText(table.getRow(0).getCell(3), "CPMK", true);

        for (int i = 0; i < ikList.size(); i++) {
            var ik = ikList.get(i);
            setCellText(table.getRow(i + 1).getCell(0), ik.getKode(), false);
            setCellText(table.getRow(i + 1).getCell(1), ik.getPernyataan(), false);
            setCellText(table.getRow(i + 1).getCell(2), ik.getMappingCpl() != null ? ik.getMappingCpl() : "", false);
            setCellText(table.getRow(i + 1).getCell(3), ik.getMappingCpmk() != null ? ik.getMappingCpmk() : "", false);
        }
    }

    private void addPenilaianTable(XWPFDocument document, List<RPSDataDTO.CPMKDTO> cpmkList) {
        if (cpmkList == null || cpmkList.isEmpty()) return;

        XWPFTable table = document.createTable(cpmkList.size() + 2, 8);
        setTableWidth(table, 9000);

        // Header row 1
        setCellText(table.getRow(0).getCell(0), "CPMK", true);
        setCellText(table.getRow(0).getCell(1), "Pernyataan", true);
        setCellText(table.getRow(0).getCell(2), "N1 (20%)", true);
        setCellText(table.getRow(0).getCell(3), "N2 (30%)", true);
        setCellText(table.getRow(0).getCell(4), "N3 (10%)", true);
        setCellText(table.getRow(0).getCell(5), "N4 (20%)", true);
        setCellText(table.getRow(0).getCell(6), "N5 (20%)", true);
        setCellText(table.getRow(0).getCell(7), "Total", true);

        int totalN1 = 0, totalN2 = 0, totalN3 = 0, totalN4 = 0, totalN5 = 0;

        for (int i = 0; i < cpmkList.size(); i++) {
            var cpmk = cpmkList.get(i);
            int n1 = cpmk.getN1() != null ? cpmk.getN1() : 0;
            int n2 = cpmk.getN2() != null ? cpmk.getN2() : 0;
            int n3 = cpmk.getN3() != null ? cpmk.getN3() : 0;
            int n4 = cpmk.getN4() != null ? cpmk.getN4() : 0;
            int n5 = cpmk.getN5() != null ? cpmk.getN5() : 0;
            int total = n1 + n2 + n3 + n4 + n5;

            totalN1 += n1;
            totalN2 += n2;
            totalN3 += n3;
            totalN4 += n4;
            totalN5 += n5;

            setCellText(table.getRow(i + 1).getCell(0), cpmk.getKode(), false);
            setCellText(table.getRow(i + 1).getCell(1), cpmk.getPernyataan(), false);
            setCellText(table.getRow(i + 1).getCell(2), String.valueOf(n1), false);
            setCellText(table.getRow(i + 1).getCell(3), String.valueOf(n2), false);
            setCellText(table.getRow(i + 1).getCell(4), String.valueOf(n3), false);
            setCellText(table.getRow(i + 1).getCell(5), String.valueOf(n4), false);
            setCellText(table.getRow(i + 1).getCell(6), String.valueOf(n5), false);
            setCellText(table.getRow(i + 1).getCell(7), String.valueOf(total), false);
        }

        // Total row
        int lastRow = cpmkList.size() + 1;
        setCellText(table.getRow(lastRow).getCell(0), "Total", true);
        setCellText(table.getRow(lastRow).getCell(1), "", false);
        setCellText(table.getRow(lastRow).getCell(2), String.valueOf(totalN1), true);
        setCellText(table.getRow(lastRow).getCell(3), String.valueOf(totalN2), true);
        setCellText(table.getRow(lastRow).getCell(4), String.valueOf(totalN3), true);
        setCellText(table.getRow(lastRow).getCell(5), String.valueOf(totalN4), true);
        setCellText(table.getRow(lastRow).getCell(6), String.valueOf(totalN5), true);
        setCellText(table.getRow(lastRow).getCell(7), String.valueOf(totalN1 + totalN2 + totalN3 + totalN4 + totalN5), true);
    }

    private void addWeeklyPlanTable(XWPFDocument document, List<RPSDataDTO.MingguDTO> mingguList) {
        if (mingguList == null || mingguList.isEmpty()) return;

        XWPFTable table = document.createTable(mingguList.size() + 1, 8);
        setTableWidth(table, 14400); // Wider table

        // Header
        String[] headers = {"Minggu", "CPMK", "Materi", "Kemampuan Akhir", "Metode", "Waktu", "Penilaian", "Bobot"};
        for (int i = 0; i < headers.length; i++) {
            setCellText(table.getRow(0).getCell(i), headers[i], true);
        }

        for (int i = 0; i < mingguList.size(); i++) {
            var minggu = mingguList.get(i);
            XWPFTableRow row = table.getRow(i + 1);

            setCellText(row.getCell(0), String.valueOf(minggu.getMinggu()), false);
            setCellText(row.getCell(1), minggu.getMappingCpmk() != null ? minggu.getMappingCpmk() : "", false);
            setCellText(row.getCell(2), minggu.getMateri() != null ? minggu.getMateri() : "", false);
            setCellText(row.getCell(3), minggu.getKemampuanAkhir() != null ? minggu.getKemampuanAkhir() : "", false);
            setCellText(row.getCell(4), minggu.getMetode() != null ? minggu.getMetode() : "", false);
            setCellText(row.getCell(5), minggu.getWaktu() != null ? minggu.getWaktu() + " menit" : "", false);
            setCellText(row.getCell(6), minggu.getPenilaian() != null ? minggu.getPenilaian() : "", false);
            
            double bobot = minggu.getBobot() != null ? minggu.getBobot() : 0;
            setCellText(row.getCell(7), String.format("%.1f%%", bobot), false);
        }
    }

    private void addReferensiList(XWPFDocument document, List<RPSDataDTO.ReferensiDTO> referensiList) {
        if (referensiList == null || referensiList.isEmpty()) return;

        for (RPSDataDTO.ReferensiDTO ref : referensiList) {
            XWPFParagraph paragraph = document.createParagraph();
            XWPFRun run = paragraph.createRun();
            
            StringBuilder sb = new StringBuilder();
            sb.append(ref.getNomor()).append(". ");
            if (ref.getPenulis() != null && !ref.getPenulis().isEmpty()) {
                sb.append(ref.getPenulis()).append(". ");
            }
            if (ref.getTahun() != null) {
                sb.append("(").append(ref.getTahun()).append("). ");
            }
            if (ref.getJudul() != null && !ref.getJudul().isEmpty()) {
                sb.append(ref.getJudul()).append(". ");
            }
            if (ref.getKota() != null && !ref.getKota().isEmpty()) {
                sb.append(ref.getKota()).append(": ");
            }
            if (ref.getPenerbit() != null && !ref.getPenerbit().isEmpty()) {
                sb.append(ref.getPenerbit()).append(".");
            }
            if (ref.getJenis() != null) {
                sb.append(" [").append(ref.getJenis()).append("]");
            }
            
            run.setText(sb.toString());
            run.setFontSize(11);
            run.setFontFamily("Times New Roman");
        }
    }

    private void setTableWidth(XWPFTable table, int width) {
        CTTbl ctTable = table.getCTTbl();
        CTTblPr tblPr = ctTable.getTblPr() != null ? ctTable.getTblPr() : ctTable.addNewTblPr();
        CTTblWidth tblWidth = tblPr.isSetTblW() ? tblPr.getTblW() : tblPr.addNewTblW();
        tblWidth.setW(BigInteger.valueOf(width));
        tblWidth.setType(STTblWidth.DXA);
    }

    private void setCellText(XWPFTableCell cell, String text, boolean bold) {
        cell.removeParagraph(0);
        XWPFParagraph paragraph = cell.addParagraph();
        XWPFRun run = paragraph.createRun();
        run.setText(text != null ? text : "");
        run.setBold(bold);
        run.setFontSize(10);
        run.setFontFamily("Times New Roman");

        // Cell styling
        CTTc ctTc = cell.getCTTc();
        CTTcPr tcPr = ctTc.isSetTcPr() ? ctTc.getTcPr() : ctTc.addNewTcPr();
        
        // Padding
        CTTcMar tcMar = tcPr.isSetTcMar() ? tcPr.getTcMar() : tcPr.addNewTcMar();
        CTTblWidth margin = CTTblWidth.Factory.newInstance();
        margin.setW(BigInteger.valueOf(100));
        margin.setType(STTblWidth.DXA);
        tcMar.setTop(margin);
        tcMar.setBottom(margin);
        tcMar.setLeft(margin);
        tcMar.setRight(margin);
    }
}
