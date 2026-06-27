package com.banco.api.controller;

import com.banco.api.dto.MovimientoReporteDTO;
import com.banco.api.dto.ReporteResponseDTO;
import com.banco.api.exception.CustomException;
import com.banco.api.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/reportes")
@CrossOrigin(origins = "*")
public class ReporteController {

    @Autowired
    private ReportService reportService;

    @GetMapping
    public ResponseEntity<List<MovimientoReporteDTO>> getReport(
            @RequestParam("fecha") String fechaStr,
            @RequestParam("cliente") String clienteStr) {
        List<MovimientoReporteDTO> reportData = getReportDataList(fechaStr, clienteStr);
        List<MovimientoReporteDTO> jsonReport = reportService.generateJsonReport(reportData);
        return ResponseEntity.ok(jsonReport);
    }

    @GetMapping("/pdf")
    public ResponseEntity<ReporteResponseDTO> getPdfReport(
            @RequestParam("fecha") String fechaStr,
            @RequestParam("cliente") String clienteStr) {
        List<MovimientoReporteDTO> reportData = getReportDataList(fechaStr, clienteStr);
        String pdfReport = reportService.generatePdfReport(reportData);
        List<MovimientoReporteDTO> jsonReport = reportService.generateJsonReport(reportData);
        return ResponseEntity.ok(new ReporteResponseDTO(pdfReport, jsonReport));
    }

    private List<MovimientoReporteDTO> getReportDataList(String fechaStr, String clienteStr) {
        if (fechaStr == null || fechaStr.trim().isEmpty()) {
            throw new CustomException("El parámetro 'fecha' es obligatorio");
        }
        if (clienteStr == null || clienteStr.trim().isEmpty()) {
            throw new CustomException("El parámetro 'cliente' es obligatorio");
        }

        // Parse date range
        String[] dates = fechaStr.split(",");
        LocalDateTime start;
        LocalDateTime end;

        if (dates.length == 1) {
            start = parseDate(dates[0], false);
            end = parseDate(dates[0], true);
        } else if (dates.length == 2) {
            start = parseDate(dates[0], false);
            end = parseDate(dates[1], true);
        } else {
            throw new CustomException("El parámetro 'fecha' debe tener formato YYYY-MM-DD o YYYY-MM-DD,YYYY-MM-DD");
        }

        // Query report data
        try {
            Integer id = Integer.parseInt(clienteStr);
            return reportService.getReportData(id, start, end);
        } catch (NumberFormatException e) {
            return reportService.getReportDataByClienteCode(clienteStr, start, end);
        }
    }

    private LocalDateTime parseDate(String dateStr, boolean endOfDay) {
        String[] patterns = {"yyyy-MM-dd", "dd/MM/yyyy", "d/M/yyyy", "yyyy/MM/dd"};
        for (String pattern : patterns) {
            try {
                LocalDate date = LocalDate.parse(dateStr.trim(), DateTimeFormatter.ofPattern(pattern));
                return endOfDay ? date.atTime(LocalTime.MAX) : date.atStartOfDay();
            } catch (Exception e) {
                // Ignore and try next pattern
            }
        }
        throw new CustomException("Formato de fecha inválido: " + dateStr + ". Use yyyy-MM-dd o dd/MM/yyyy.");
    }
}
