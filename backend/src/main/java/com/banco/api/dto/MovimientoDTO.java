package com.banco.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoDTO {

    private Integer id;

    private LocalDateTime fecha;

    @NotBlank(message = "El tipo de movimiento es obligatorio")
    @Size(max = 50, message = "El tipo de movimiento no puede exceder los 50 caracteres")
    private String tipoMovimiento; // 'Retiro' or 'Deposito'

    @NotNull(message = "El valor es obligatorio")
    private BigDecimal valor;

    private BigDecimal saldo; // Balance after movement

    @NotBlank(message = "El número de cuenta es obligatorio")
    private String cuentaNumero;
}
