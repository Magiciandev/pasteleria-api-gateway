package com.example.producto_service.dto;

import com.example.producto_service.model.DetalleProducto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetalleProductoDTO {
    private Long id;
    private String descripcion;
    private Boolean contieneLactosa;
    private Boolean contieneHuevos;
    private Boolean contieneFrutosSecos;
    private Boolean contieneGluten;
    private String alergenos;
    private String instruccionesAlmacenamiento;
    private String presentacion;

    public DetalleProducto toModel() {
        return new DetalleProducto(id, descripcion, contieneLactosa, contieneHuevos, 
                contieneFrutosSecos, contieneGluten, alergenos, instruccionesAlmacenamiento, presentacion);
    }

    public static DetalleProductoDTO fromModel(DetalleProducto d) {
        if (d == null) return null;
        return new DetalleProductoDTO(d.getId(), d.getDescripcion(), d.getContieneLactosa(), 
                d.getContieneHuevos(), d.getContieneFrutosSecos(), d.getContieneGluten(), 
                d.getAlergenos(), d.getInstruccionesAlmacenamiento(), d.getPresentacion());
    }
}
