package com.banco.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReporteResponseDTO {
    private String pdf; // Base64 encoded PDF
    private List<MovimientoReporteDTO> reporte; // JSON List
}
