package com.example.producto_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "detalle_producto")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String descripcion;
    private Boolean contieneLactosa;
    private Boolean contieneHuevos;
    private Boolean contieneFrutosSecos;
    private Boolean contieneGluten;
    private String alergenos;
    private String instruccionesAlmacenamiento;
    private String presentacion;
}