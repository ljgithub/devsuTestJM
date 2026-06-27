package com.banco.api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "cliente")
@PrimaryKeyJoinColumn(name = "id")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cliente extends Persona {

    @NotBlank(message = "El ID del cliente es obligatorio")
    @Size(max = 50, message = "El ID del cliente no puede exceder los 50 caracteres")
    @Column(name = "clienteid", nullable = false, unique = true, length = 50)
    private String clienteid;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(max = 100, message = "La contraseña no puede exceder los 100 caracteres")
    @Column(nullable = false, length = 100)
    private String contrasena;

    @NotNull(message = "El estado es obligatorio")
    @Column(nullable = false)
    private Boolean estado;
}
