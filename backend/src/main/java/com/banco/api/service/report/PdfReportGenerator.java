package com.banco.api.service.report;

import com.banco.api.dto.MovimientoReporteDTO;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.springframework.stereotype.Component;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.List;

@Component
public class PdfReportGenerator implements ReportGeneratorStrategy<String> {

    @Override
    public String generate(List<MovimientoReporteDTO> reportData) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, out);
            document.open();

            // Setup fonts
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9);
            Font contentFont = FontFactory.getFont(FontFactory.HELVETICA, 8);

            // Title
            Paragraph title = new Paragraph("ESTADO DE CUENTA - REPORTE DE MOVIMIENTOS", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // Table with 8 columns
            PdfPTable table = new PdfPTable(8);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{1.5f, 2.5f, 1.5f, 1.5f, 1.5f, 1.0f, 1.5f, 1.5f});

            // Column Headers
            String[] headers = {
                    "Fecha", 
                    "Cliente", 
                    "Numero Cuenta", 
                    "Tipo", 
                    "Saldo Inicial", 
                    "Estado", 
                    "Movimiento", 
                    "Saldo Disponible"
            };

            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headFont));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(8);
                cell.setBackgroundColor(java.awt.Color.LIGHT_GRAY);
                table.addCell(cell);
            }

            // Populate table rows
            for (MovimientoReporteDTO row : reportData) {
                table.addCell(createCell(row.getFecha(), contentFont, Element.ALIGN_CENTER));
                table.addCell(createCell(row.getCliente(), contentFont, Element.ALIGN_LEFT));
                table.addCell(createCell(row.getNumeroCuenta(), contentFont, Element.ALIGN_CENTER));
                table.addCell(createCell(row.getTipo(), contentFont, Element.ALIGN_CENTER));
                table.addCell(createCell(row.getSaldoInicial().toString(), contentFont, Element.ALIGN_RIGHT));
                table.addCell(createCell(row.getEstado() ? "True" : "False", contentFont, Element.ALIGN_CENTER));
                table.addCell(createCell(row.getMovimiento().toString(), contentFont, Element.ALIGN_RIGHT));
                table.addCell(createCell(row.getSaldoDisponible().toString(), contentFont, Element.ALIGN_RIGHT));
            }

            document.add(table);
            document.close();

            // Encode to Base64
            return Base64.getEncoder().encodeToString(out.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException("Error al generar el PDF del reporte: " + e.getMessage(), e);
        }
    }

    private PdfPCell createCell(String text, Font font, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text != null ? text : "", font));
        cell.setHorizontalAlignment(alignment);
        cell.setPadding(5);
        return cell;
    }
}
