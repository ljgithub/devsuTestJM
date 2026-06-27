package com.banco.api.controller;

import com.banco.api.dto.MovimientoDTO;
import com.banco.api.service.MovimientoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movimientos")
@CrossOrigin(origins = "*")
public class MovimientoController {

    @Autowired
    private MovimientoService movimientoService;

    @GetMapping
    public ResponseEntity<List<MovimientoDTO>> getAll() {
        return ResponseEntity.ok(movimientoService.getAllMovimientos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovimientoDTO> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(movimientoService.getMovimientoById(id));
    }

    @PostMapping
    public ResponseEntity<MovimientoDTO> create(@Valid @RequestBody MovimientoDTO dto) {
        return new ResponseEntity<>(movimientoService.createMovimiento(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MovimientoDTO> update(
            @PathVariable Integer id, 
            @Valid @RequestBody MovimientoDTO dto) {
        return ResponseEntity.ok(movimientoService.updateMovimiento(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        movimientoService.deleteMovimiento(id);
        return ResponseEntity.noContent().build();
    }
}
