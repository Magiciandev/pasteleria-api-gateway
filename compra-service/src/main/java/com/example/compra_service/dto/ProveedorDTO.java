package com.example.compra_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO liviano usado unicamente para consumir la respuesta
 * del endpoint GET /proveedores/{id} de proveedor-service.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProveedorDTO {
    private Long id;
    private String nombre;
    private String rut;
    private String correo;
    private String telefono;
    private String direccion;
    private Boolean activo;
}
