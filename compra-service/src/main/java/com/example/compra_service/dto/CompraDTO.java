package com.example.compra_service.dto;

import com.example.compra_service.model.Compra;
import com.example.compra_service.model.DetalleCompra;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
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
public class CompraDTO {
    private Long id;

    @NotNull(message = "El id del proveedor es obligatorio")
    private Long idProveedor;

    private String nombreProveedor;

    @NotBlank(message = "El número de factura es obligatorio")
    private String numeroFactura;

    private LocalDateTime fecha;

    @NotEmpty(message = "La compra debe tener al menos un detalle")
    @Valid
    private List<DetalleCompraDTO> detalles;

    private Double montoTotal;
    private String estado;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Compra toModel() {
        Compra compra = new Compra();
        compra.setId(id);
        compra.setIdProveedor(idProveedor);
        compra.setNombreProveedor(nombreProveedor);
        compra.setNumeroFactura(numeroFactura);
        compra.setFecha(fecha);
        compra.setMontoTotal(montoTotal);
        compra.setEstado(estado);
        if (detalles != null) {
            List<DetalleCompra> detallesModel = detalles.stream()
                    .map(dto -> dto.toModel(compra))
                    .collect(Collectors.toList());
            compra.setDetalles(detallesModel);
        }
        return compra;
    }

    public static CompraDTO fromModel(Compra c) {
        if (c == null) return null;
        List<DetalleCompraDTO> detallesDto = c.getDetalles() != null
                ? c.getDetalles().stream().map(DetalleCompraDTO::fromModel).collect(Collectors.toList())
                : new ArrayList<>();
        return CompraDTO.builder()
                .id(c.getId())
                .idProveedor(c.getIdProveedor())
                .nombreProveedor(c.getNombreProveedor())
                .numeroFactura(c.getNumeroFactura())
                .fecha(c.getFecha())
                .detalles(detallesDto)
                .montoTotal(c.getMontoTotal())
                .estado(c.getEstado())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .build();
    }
}
