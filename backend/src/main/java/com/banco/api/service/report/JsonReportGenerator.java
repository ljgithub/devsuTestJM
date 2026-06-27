package com.banco.api.service.report;

import com.banco.api.dto.MovimientoReporteDTO;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class JsonReportGenerator implements ReportGeneratorStrategy<List<MovimientoReporteDTO>> {
    @Override
    public List<MovimientoReporteDTO> generate(List<MovimientoReporteDTO> reportData) {
        return reportData;
    }
}
