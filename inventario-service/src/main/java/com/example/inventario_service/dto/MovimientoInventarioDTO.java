package com.example.inventario_service.dto;

import com.example.inventario_service.model.MovimientoInventario;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovimientoInventarioDTO {

    private Long id;

    @NotNull(message = "El id del producto es obligatorio")
    private Long idProducto;

    @NotBlank(message = "El nombre del producto es obligatorio")
    private String nombreProducto;

    @NotBlank(message = "El tipo de movimiento es obligatorio")
    private String tipo;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer cantidad;

    private Integer stockResultante;

    private Long idProveedor;

    private String motivo;

    private LocalDateTime fecha;

    public MovimientoInventario toModel() {
        MovimientoInventario mov = new MovimientoInventario();
        mov.setId(id);
        mov.setIdProducto(idProducto);
        mov.setNombreProducto(nombreProducto);
        mov.setTipo(tipo);
        mov.setCantidad(cantidad);
        mov.setStockResultante(stockResultante);
        mov.setIdProveedor(idProveedor);
        mov.setMotivo(motivo);
        mov.setFecha(fecha);
        return mov;
    }

    public static MovimientoInventarioDTO fromModel(MovimientoInventario m) {
        if (m == null) return null;
        return new MovimientoInventarioDTO(
                m.getId(),
                m.getIdProducto(),
                m.getNombreProducto(),
                m.getTipo(),
                m.getCantidad(),
                m.getStockResultante(),
                m.getIdProveedor(),
                m.getMotivo(),
                m.getFecha()
        );
    }
}
