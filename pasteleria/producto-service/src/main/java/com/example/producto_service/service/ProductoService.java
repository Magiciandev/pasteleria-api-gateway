package com.example.producto_service.service;

import org.springframework.stereotype.Service;
import com.example.producto_service.model.Producto;
import com.example.producto_service.model.DetalleProducto;
import com.example.producto_service.repository.ProductoRepository;
import com.example.producto_service.repository.DetalleProductoRepository;
import java.util.List;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final DetalleProductoRepository detalleProductoRepository;

    public ProductoService(ProductoRepository productoRepository, DetalleProductoRepository detalleProductoRepository) {
        this.productoRepository = productoRepository;
        this.detalleProductoRepository = detalleProductoRepository;
    }

    public Producto guardar(Producto producto) {
        if (producto.getDetalleProducto() != null) {
            DetalleProducto detalleGuardado = detalleProductoRepository.save(producto.getDetalleProducto());
            producto.setDetalleProducto(detalleGuardado);
        }
        return productoRepository.save(producto);
    }

    public List<Producto> listar() {
        return productoRepository.findAll();
    }

    public Producto buscarPorId(Long id) {
        return productoRepository.findById(id).orElse(null);
    }

    public boolean existePorId(Long id) {
        return productoRepository.existsById(id);
    }

    public Producto actualizar(Long id, Producto detalles) {
        Producto producto = productoRepository.findById(id).orElse(null);
        if (producto != null) {
            producto.setNombre(detalles.getNombre());
            producto.setPrecio(detalles.getPrecio());
            producto.setStock(detalles.getStock());
            
            if (detalles.getDetalleProducto() != null) {
                if (producto.getDetalleProducto() != null) {
                    // Actualizar detalle existente
                    DetalleProducto detalle = producto.getDetalleProducto();
                    DetalleProducto nuevosDetalles = detalles.getDetalleProducto();
                    detalle.setDescripcion(nuevosDetalles.getDescripcion());
                    detalle.setContieneLactosa(nuevosDetalles.getContieneLactosa());
                    detalle.setContieneHuevos(nuevosDetalles.getContieneHuevos());
                    detalle.setContieneFrutosSecos(nuevosDetalles.getContieneFrutosSecos());
                    detalle.setContieneGluten(nuevosDetalles.getContieneGluten());
                    detalle.setAlergenos(nuevosDetalles.getAlergenos());
                    detalle.setInstruccionesAlmacenamiento(nuevosDetalles.getInstruccionesAlmacenamiento());
                    detalle.setPresentacion(nuevosDetalles.getPresentacion());
                } else {
                    // Crear nuevo detalle
                    DetalleProducto detalleGuardado = detalleProductoRepository.save(detalles.getDetalleProducto());
                    producto.setDetalleProducto(detalleGuardado);
                }
            }
            return productoRepository.save(producto);
        }
        return null;
    }

    public void eliminar(Long id) {
        productoRepository.deleteById(id);
    }
}