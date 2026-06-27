package com.banco.api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "movimiento")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Movimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "La fecha es obligatoria")
    @Column(nullable = false)
    private LocalDateTime fecha;

    @NotBlank(message = "El tipo de movimiento es obligatorio")
    @Size(max = 50, message = "El tipo de movimiento no puede exceder los 50 caracteres")
    @Column(name = "tipo_movimiento", nullable = false, length = 50)
    private String tipoMovimiento; // 'Retiro' or 'Deposito'

    @NotNull(message = "El valor del movimiento es obligatorio")
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal valor;

    @NotNull(message = "El saldo es obligatorio")
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal saldo; // Available balance after movement

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cuenta_numero", nullable = false)
    @NotNull(message = "La cuenta es obligatoria")
    private Cuenta cuenta;
}
