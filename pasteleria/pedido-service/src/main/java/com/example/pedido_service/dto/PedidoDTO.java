package com.example.pedido_service.dto;

import com.example.pedido_service.model.Pedido;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoDTO {
    private Long id;
    private Long idCliente;
    private String nombreCliente;
    private LocalDateTime fecha;
    private String detallePedido;
    private Double total;
    private String estado;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Pedido toModel() {
        Pedido pedido = new Pedido();
        pedido.setId(id);
        pedido.setIdCliente(idCliente);
        pedido.setNombreCliente(nombreCliente);
        pedido.setFecha(fecha);
        pedido.setDetallePedido(detallePedido);
        pedido.setTotal(total);
        pedido.setEstado(estado);
        return pedido;
    }

    public static PedidoDTO fromModel(Pedido p) {
        if (p == null) return null;
        return PedidoDTO.builder()
            .id(p.getId())
            .idCliente(p.getIdCliente())
            .nombreCliente(p.getNombreCliente())
            .fecha(p.getFecha())
            .detallePedido(p.getDetallePedido())
            .total(p.getTotal())
            .estado(p.getEstado())
            .createdAt(p.getCreatedAt())
            .updatedAt(p.getUpdatedAt())
            .build();
    }
}
