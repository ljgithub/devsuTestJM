package com.banco.api.service;

import com.banco.api.dto.MovimientoReporteDTO;
import com.banco.api.entity.Cliente;
import com.banco.api.entity.Movimiento;
import com.banco.api.exception.CustomException;
import com.banco.api.repository.ClienteRepository;
import com.banco.api.repository.MovimientoRepository;
import com.banco.api.service.report.JsonReportGenerator;
import com.banco.api.service.report.PdfReportGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportService {

    @Autowired
    private MovimientoRepository movimientoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private JsonReportGenerator jsonReportGenerator;

    @Autowired
    private PdfReportGenerator pdfReportGenerator;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Transactional(readOnly = true)
    public List<MovimientoReporteDTO> getReportData(Integer clienteId, LocalDateTime start, LocalDateTime end) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new CustomException("Cliente no encontrado con ID: " + clienteId));

        List<Movimiento> movimientos = movimientoRepository.findByCuentaClienteIdAndFechaBetweenOrderByFechaDesc(
                cliente.getId(), start, end);

        return movimientos.stream()
                .map(m -> {
                    MovimientoReporteDTO dto = new MovimientoReporteDTO();
                    dto.setFecha(m.getFecha().format(DATE_FORMATTER));
                    dto.setCliente(m.getCuenta().getCliente().getNombre());
                    dto.setNumeroCuenta(m.getCuenta().getNumeroCuenta());
                    dto.setTipo(m.getCuenta().getTipoCuenta());
                    dto.setSaldoInicial(m.getSaldo().subtract(m.getValor()));
                    dto.setEstado(m.getCuenta().getEstado());
                    dto.setMovimiento(m.getValor());
                    dto.setSaldoDisponible(m.getSaldo());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MovimientoReporteDTO> getReportDataByClienteCode(String clienteid, LocalDateTime start, LocalDateTime end) {
        Cliente cliente = clienteRepository.findByClienteid(clienteid)
                .orElseThrow(() -> new CustomException("Cliente no encontrado con Código: " + clienteid));

        List<Movimiento> movimientos = movimientoRepository.findByCuentaClienteIdAndFechaBetweenOrderByFechaDesc(
                cliente.getId(), start, end);

        return movimientos.stream()
                .map(m -> {
                    MovimientoReporteDTO dto = new MovimientoReporteDTO();
                    dto.setFecha(m.getFecha().format(DATE_FORMATTER));
                    dto.setCliente(m.getCuenta().getCliente().getNombre());
                    dto.setNumeroCuenta(m.getCuenta().getNumeroCuenta());
                    dto.setTipo(m.getCuenta().getTipoCuenta());
                    dto.setSaldoInicial(m.getSaldo().subtract(m.getValor()));
                    dto.setEstado(m.getCuenta().getEstado());
                    dto.setMovimiento(m.getValor());
                    dto.setSaldoDisponible(m.getSaldo());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<MovimientoReporteDTO> generateJsonReport(List<MovimientoReporteDTO> data) {
        return jsonReportGenerator.generate(data);
    }

    public String generatePdfReport(List<MovimientoReporteDTO> data) {
        return pdfReportGenerator.generate(data);
    }
}
