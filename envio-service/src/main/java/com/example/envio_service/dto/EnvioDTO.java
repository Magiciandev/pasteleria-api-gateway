package com.example.envio_service.dto;

import com.example.envio_service.model.Envio;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnvioDTO {
    private Long id;
    private Long idPedido;
    private Long idCliente;
    private String nombreCliente;
    private String direccionEntrega;
    private String estado;
    private LocalDateTime fechaEnvio;
    private LocalDateTime fechaEntregaEstimada;
    private LocalDateTime fechaEntregaReal;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Envio toModel() {
        Envio envio = new Envio();
        envio.setId(id);
        envio.setIdPedido(idPedido);
        envio.setIdCliente(idCliente);
        envio.setNombreCliente(nombreCliente);
        envio.setDireccionEntrega(direccionEntrega);
        envio.setEstado(estado);
        envio.setFechaEnvio(fechaEnvio);
        envio.setFechaEntregaEstimada(fechaEntregaEstimada);
        envio.setFechaEntregaReal(fechaEntregaReal);
        envio.setCreatedAt(createdAt);
        envio.setUpdatedAt(updatedAt);
        return envio;
    }

    public static EnvioDTO fromModel(Envio e) {
        if (e == null) return null;
        return EnvioDTO.builder()
            .id(e.getId())
            .idPedido(e.getIdPedido())
            .idCliente(e.getIdCliente())
            .nombreCliente(e.getNombreCliente())
            .direccionEntrega(e.getDireccionEntrega())
            .estado(e.getEstado())
            .fechaEnvio(e.getFechaEnvio())
            .fechaEntregaEstimada(e.getFechaEntregaEstimada())
            .fechaEntregaReal(e.getFechaEntregaReal())
            .createdAt(e.getCreatedAt())
            .updatedAt(e.getUpdatedAt())
            .build();
    }
}

