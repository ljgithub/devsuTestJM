package com.banco.api.service.report;

import com.banco.api.dto.MovimientoReporteDTO;
import java.util.List;

public interface ReportGeneratorStrategy<T> {
    T generate(List<MovimientoReporteDTO> reportData);
}
