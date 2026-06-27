package com.banco.api.service;

import com.banco.api.dto.MovimientoDTO;
import com.banco.api.entity.Cuenta;
import com.banco.api.entity.Movimiento;
import com.banco.api.exception.CustomException;
import com.banco.api.repository.CuentaRepository;
import com.banco.api.repository.MovimientoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MovimientoService {

    @Autowired
    private MovimientoRepository movimientoRepository;

    @Autowired
    private CuentaRepository cuentaRepository;

    @Transactional(readOnly = true)
    public List<MovimientoDTO> getAllMovimientos() {
        return movimientoRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MovimientoDTO getMovimientoById(Integer id) {
        Movimiento m = movimientoRepository.findById(id)
                .orElseThrow(() -> new CustomException("Movimiento no encontrado con ID: " + id));
        return convertToDTO(m);
    }

    @Transactional
    public MovimientoDTO createMovimiento(MovimientoDTO dto) {
        Cuenta cuenta = cuentaRepository.findByNumeroCuenta(dto.getCuentaNumero())
                .orElseThrow(() -> new CustomException("Cuenta no encontrada con número: " + dto.getCuentaNumero()));

        if (!cuenta.getEstado()) {
            throw new CustomException("La cuenta está inactiva y no puede recibir movimientos");
        }

        // Get latest balance of the account
        List<Movimiento> movimientos = movimientoRepository.findByCuentaNumeroCuentaOrderByFechaDesc(cuenta.getNumeroCuenta());
        BigDecimal currentBalance = movimientos.isEmpty() ? cuenta.getSaldoInicial() : movimientos.get(0).getSaldo();

        BigDecimal valor = dto.getValor();
        boolean isDebit = valor.compareTo(BigDecimal.ZERO) < 0;

        // Validations for Debit
        if (isDebit) {
            // 1. Check if sufficient balance
            if (currentBalance.compareTo(BigDecimal.ZERO) <= 0 || currentBalance.add(valor).compareTo(BigDecimal.ZERO) < 0) {
                throw new CustomException("Saldo no disponible");
            }

            // 2. Check daily limit ($1000)
            LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
            LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);
            BigDecimal totalDebitsToday = movimientoRepository.sumDebitsByClienteAndDay(
                    cuenta.getCliente().getId(), startOfDay, endOfDay);
            
            // Note: totalDebitsToday is negative or zero, let's work with absolute values
            BigDecimal absDebitsToday = totalDebitsToday.abs();
            BigDecimal absCurrentDebit = valor.abs();
            BigDecimal limit = new BigDecimal("1000.00");

            if (absDebitsToday.add(absCurrentDebit).compareTo(limit) > 0) {
                throw new CustomException("Cupo diario Excedido");
            }
        }

        // Calculate new balance
        BigDecimal newBalance = currentBalance.add(valor);

        // Map and save Movimiento
        Movimiento movimiento = new Movimiento();
        movimiento.setFecha(dto.getFecha() != null ? dto.getFecha() : LocalDateTime.now());
        movimiento.setTipoMovimiento(dto.getTipoMovimiento());
        movimiento.setValor(valor);
        movimiento.setSaldo(newBalance);
        movimiento.setCuenta(cuenta);

        Movimiento saved = movimientoRepository.save(movimiento);
        recalculateBalances(cuenta.getNumeroCuenta());
        Movimiento reloaded = movimientoRepository.findById(saved.getId()).orElse(saved);
        return convertToDTO(reloaded);
    }

    @Transactional
    public MovimientoDTO updateMovimiento(Integer id, MovimientoDTO dto) {
        Movimiento existing = movimientoRepository.findById(id)
                .orElseThrow(() -> new CustomException("Movimiento no encontrado con ID: " + id));
        
        // When updating a transaction, we allow changing the value, date, and type
        existing.setTipoMovimiento(dto.getTipoMovimiento());
        existing.setValor(dto.getValor());
        if (dto.getFecha() != null) {
            existing.setFecha(dto.getFecha());
        }
        
        Movimiento saved = movimientoRepository.save(existing);
        recalculateBalances(existing.getCuenta().getNumeroCuenta());
        
        Movimiento reloaded = movimientoRepository.findById(saved.getId()).orElse(saved);
        return convertToDTO(reloaded);
    }

    @Transactional
    public void deleteMovimiento(Integer id) {
        Movimiento existing = movimientoRepository.findById(id)
                .orElseThrow(() -> new CustomException("Movimiento no encontrado con ID: " + id));
        String numeroCuenta = existing.getCuenta().getNumeroCuenta();
        
        movimientoRepository.delete(existing);
        recalculateBalances(numeroCuenta);
    }

    private void recalculateBalances(String numeroCuenta) {
        Cuenta cuenta = cuentaRepository.findByNumeroCuenta(numeroCuenta)
                .orElseThrow(() -> new CustomException("Cuenta no encontrada con número: " + numeroCuenta));
        
        List<Movimiento> movimientos = movimientoRepository.findByCuentaNumeroCuentaOrderByFechaAsc(numeroCuenta);
        BigDecimal currentBalance = cuenta.getSaldoInicial();
        for (Movimiento m : movimientos) {
            currentBalance = currentBalance.add(m.getValor());
            if (currentBalance.compareTo(BigDecimal.ZERO) < 0) {
                throw new CustomException("Saldo no disponible");
            }
            m.setSaldo(currentBalance);
        }
        movimientoRepository.saveAll(movimientos);
    }


    private MovimientoDTO convertToDTO(Movimiento m) {
        return new MovimientoDTO(
                m.getId(),
                m.getFecha(),
                m.getTipoMovimiento(),
                m.getValor(),
                m.getSaldo(),
                m.getCuenta().getNumeroCuenta()
        );
    }
}
