package com.example.producto_service.dto;

import com.example.producto_service.model.Producto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoDTO {
    private Long id;
    private String nombre;
    private Double precio;
    private Integer stock;
    private DetalleProductoDTO detalleProducto;

    public Producto toModel() {
        Producto producto = new Producto();
        producto.setId(id);
        producto.setNombre(nombre);
        producto.setPrecio(precio);
        producto.setStock(stock);
        if (detalleProducto != null) {
            producto.setDetalleProducto(detalleProducto.toModel());
        }
        return producto;
    }

    public static ProductoDTO fromModel(Producto p) {
        if (p == null) return null;
        return new ProductoDTO(
            p.getId(), 
            p.getNombre(), 
            p.getPrecio(), 
            p.getStock(),
            DetalleProductoDTO.fromModel(p.getDetalleProducto())
        );
    }
}