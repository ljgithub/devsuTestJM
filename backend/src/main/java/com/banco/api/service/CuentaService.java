package com.banco.api.service;

import com.banco.api.dto.CuentaDTO;
import com.banco.api.entity.Cliente;
import com.banco.api.entity.Cuenta;
import com.banco.api.exception.CustomException;
import com.banco.api.repository.ClienteRepository;
import com.banco.api.repository.CuentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CuentaService {

    @Autowired
    private CuentaRepository cuentaRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Transactional(readOnly = true)
    public List<CuentaDTO> getAllCuentas() {
        return cuentaRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CuentaDTO getCuentaByNumero(String numeroCuenta) {
        Cuenta cuenta = cuentaRepository.findByNumeroCuenta(numeroCuenta)
                .orElseThrow(() -> new CustomException("Cuenta no encontrada con número: " + numeroCuenta));
        return convertToDTO(cuenta);
    }

    @Transactional
    public CuentaDTO createCuenta(CuentaDTO dto) {
        if (cuentaRepository.findByNumeroCuenta(dto.getNumeroCuenta()).isPresent()) {
            throw new CustomException("El número de cuenta ya está registrado");
        }
        
        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new CustomException("Cliente no encontrado con ID: " + dto.getClienteId()));

        Cuenta cuenta = new Cuenta();
        cuenta.setNumeroCuenta(dto.getNumeroCuenta());
        cuenta.setTipoCuenta(dto.getTipoCuenta());
        cuenta.setSaldoInicial(dto.getSaldoInicial());
        cuenta.setEstado(dto.getEstado());
        cuenta.setCliente(cliente);

        Cuenta saved = cuentaRepository.save(cuenta);
        return convertToDTO(saved);
    }

    @Transactional
    public CuentaDTO updateCuenta(String numeroCuenta, CuentaDTO dto) {
        Cuenta existing = cuentaRepository.findByNumeroCuenta(numeroCuenta)
                .orElseThrow(() -> new CustomException("Cuenta no encontrada con número: " + numeroCuenta));

        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new CustomException("Cliente no encontrado con ID: " + dto.getClienteId()));

        existing.setTipoCuenta(dto.getTipoCuenta());
        existing.setSaldoInicial(dto.getSaldoInicial());
        existing.setEstado(dto.getEstado());
        existing.setCliente(cliente);

        Cuenta updated = cuentaRepository.save(existing);
        return convertToDTO(updated);
    }

    @Transactional
    public void deleteCuenta(String numeroCuenta) {
        if (!cuentaRepository.existsById(numeroCuenta)) {
            throw new CustomException("Cuenta no encontrada con número: " + numeroCuenta);
        }
        cuentaRepository.deleteById(numeroCuenta);
    }

    private CuentaDTO convertToDTO(Cuenta cuenta) {
        return new CuentaDTO(
                cuenta.getNumeroCuenta(),
                cuenta.getTipoCuenta(),
                cuenta.getSaldoInicial(),
                cuenta.getEstado(),
                cuenta.getCliente().getId(),
                cuenta.getCliente().getNombre()
        );
    }
}
