package com.example.pedido_service.dto;

import com.example.pedido_service.model.DetallePedido;
import com.example.pedido_service.model.Pedido;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetallePedidoDTO {
    private Long id;
    private Long idProducto;
    private String nombreProducto;
    private Integer cantidad;
    private Double precioUnitario;
    private Double precioTotal;

    public DetallePedido toModel(Pedido pedido) {
        DetallePedido detalle = new DetallePedido();
        detalle.setId(id);
        detalle.setPedido(pedido);
        detalle.setIdProducto(idProducto);
        detalle.setNombreProducto(nombreProducto);
        detalle.setCantidad(cantidad);
        detalle.setPrecioUnitario(precioUnitario);
        detalle.setPrecioTotal(precioTotal != null ? precioTotal : cantidad * precioUnitario);
        return detalle;
    }

    public static DetallePedidoDTO fromModel(DetallePedido d) {
        if (d == null) return null;
        return DetallePedidoDTO.builder()
            .id(d.getId())
            .idProducto(d.getIdProducto())
            .nombreProducto(d.getNombreProducto())
            .cantidad(d.getCantidad())
            .precioUnitario(d.getPrecioUnitario())
            .precioTotal(d.getPrecioTotal())
            .build();
    }
}