package com.example.pedido_service.dto;

import com.example.pedido_service.model.DetallePedido;
import com.example.pedido_service.model.Pedido;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoDTO {
    private Long id;

    @NotNull(message = "El id del cliente es obligatorio")
    private Long idCliente;

    private String nombreCliente;
    private LocalDateTime fecha;

    @NotEmpty(message = "El pedido debe tener al menos un detalle")
    @Valid
    private List<DetallePedidoDTO> detalles;

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
        pedido.setTotal(total);
        pedido.setEstado(estado);
        if (detalles != null) {
            List<DetallePedido> detallesModel = detalles.stream()
                    .map(dto -> dto.toModel(pedido))
                    .collect(Collectors.toList());
            pedido.setDetalles(detallesModel);
        }
        return pedido;
    }

    public static PedidoDTO fromModel(Pedido p) {
        if (p == null) return null;
        List<DetallePedidoDTO> detallesDto = p.getDetalles() != null
                ? p.getDetalles().stream().map(DetallePedidoDTO::fromModel).collect(Collectors.toList())
                : new ArrayList<>();
        return PedidoDTO.builder()
            .id(p.getId())
            .idCliente(p.getIdCliente())
            .nombreCliente(p.getNombreCliente())
            .fecha(p.getFecha())
            .detalles(detallesDto)
            .total(p.getTotal())
            .estado(p.getEstado())
            .createdAt(p.getCreatedAt())
            .updatedAt(p.getUpdatedAt())
            .build();
    }
}
