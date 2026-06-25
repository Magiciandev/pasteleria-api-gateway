package com.example.proveedor_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Proveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del proveedor es obligatorio")
    private String nombre;

    @NotBlank(message = "El RUT del proveedor es obligatorio")
    @Column(unique = true)
    private String rut;

    @NotBlank(message = "El correo del proveedor es obligatorio")
    @Email(message = "El correo debe tener un formato válido")
    private String correo;

    private String telefono;

    private String direccion;

    @NotNull(message = "El estado activo es obligatorio")
    private Boolean activo;

    @NotNull(message = "La fecha de registro es obligatoria")
    private LocalDate fechaRegistro;
}
