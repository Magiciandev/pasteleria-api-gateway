package com.example.compra_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO liviano usado unicamente para construir el cuerpo del
 * POST /inventario que compra-service dispara hacia inventario-service
 * al marcar una compra como RECIBIDA (carga de stock, tipo ENTRADA).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovimientoInventarioDTO {
    private Long idProducto;
    private String nombreProducto;
    private String tipo;
    private Integer cantidad;
    private Integer stockResultante;
    private Long idProveedor;
    private String motivo;
    private LocalDateTime fecha;
}
