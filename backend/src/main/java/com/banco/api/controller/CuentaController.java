package com.banco.api.controller;

import com.banco.api.dto.CuentaDTO;
import com.banco.api.service.CuentaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cuentas")
@CrossOrigin(origins = "*")
public class CuentaController {

    @Autowired
    private CuentaService cuentaService;

    @GetMapping
    public ResponseEntity<List<CuentaDTO>> getAll() {
        return ResponseEntity.ok(cuentaService.getAllCuentas());
    }

    @GetMapping("/{numeroCuenta}")
    public ResponseEntity<CuentaDTO> getByNumero(@PathVariable String numeroCuenta) {
        return ResponseEntity.ok(cuentaService.getCuentaByNumero(numeroCuenta));
    }

    @PostMapping
    public ResponseEntity<CuentaDTO> create(@Valid @RequestBody CuentaDTO dto) {
        return new ResponseEntity<>(cuentaService.createCuenta(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{numeroCuenta}")
    public ResponseEntity<CuentaDTO> update(
            @PathVariable String numeroCuenta, 
            @Valid @RequestBody CuentaDTO dto) {
        return ResponseEntity.ok(cuentaService.updateCuenta(numeroCuenta, dto));
    }

    @DeleteMapping("/{numeroCuenta}")
    public ResponseEntity<Void> delete(@PathVariable String numeroCuenta) {
        cuentaService.deleteCuenta(numeroCuenta);
        return ResponseEntity.noContent().build();
    }
}
