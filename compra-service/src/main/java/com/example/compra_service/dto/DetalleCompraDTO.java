package com.example.compra_service.dto;

import com.example.compra_service.model.Compra;
import com.example.compra_service.model.DetalleCompra;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetalleCompraDTO {
    private Long id;

    @NotNull(message = "El id del producto es obligatorio")
    private Long idProducto;

    @NotBlank(message = "El nombre del producto es obligatorio")
    private String nombreProducto;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer cantidad;

    @NotNull(message = "El precio unitario es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio unitario debe ser mayor a 0")
    private Double precioUnitario;

    private Double precioTotal;

    public DetalleCompra toModel(Compra compra) {
        DetalleCompra detalle = new DetalleCompra();
        detalle.setId(id);
        detalle.setCompra(compra);
        detalle.setIdProducto(idProducto);
        detalle.setNombreProducto(nombreProducto);
        detalle.setCantidad(cantidad);
        detalle.setPrecioUnitario(precioUnitario);
        detalle.setPrecioTotal(precioTotal != null ? precioTotal : cantidad * precioUnitario);
        return detalle;
    }

    public static DetalleCompraDTO fromModel(DetalleCompra d) {
        if (d == null) return null;
        return DetalleCompraDTO.builder()
                .id(d.getId())
                .idProducto(d.getIdProducto())
                .nombreProducto(d.getNombreProducto())
                .cantidad(d.getCantidad())
                .precioUnitario(d.getPrecioUnitario())
                .precioTotal(d.getPrecioTotal())
                .build();
    }
}
