package com.example.producto_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.example.producto_service.exception.ResourceNotFoundException;
import com.example.producto_service.model.Producto;
import com.example.producto_service.model.DetalleProducto;
import com.example.producto_service.repository.ProductoRepository;
import com.example.producto_service.repository.DetalleProductoRepository;
import java.util.List;

@Service
public class ProductoService {

    private static final Logger logger = LoggerFactory.getLogger(ProductoService.class);

    private final ProductoRepository productoRepository;
    private final DetalleProductoRepository detalleProductoRepository;

    public ProductoService(ProductoRepository productoRepository, DetalleProductoRepository detalleProductoRepository) {
        this.productoRepository = productoRepository;
        this.detalleProductoRepository = detalleProductoRepository;
    }

    public Producto guardar(Producto producto) {
        logger.info("Guardando producto: nombre={}", producto.getNombre());
        if (producto.getDetalleProducto() != null) {
            DetalleProducto detalleGuardado = detalleProductoRepository.save(producto.getDetalleProducto());
            producto.setDetalleProducto(detalleGuardado);
        }
        Producto guardado = productoRepository.save(producto);
        logger.info("Producto guardado exitosamente id={}", guardado.getId());
        return guardado;
    }

    public List<Producto> listar() {
        logger.info("Listando todos los productos");
        List<Producto> productos = productoRepository.findAll();
        logger.info("Total productos: {}", productos.size());
        return productos;
    }

    public Producto buscarPorId(Long id) {
        logger.info("Buscando producto id={}", id);
        return productoRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Producto no encontrado id={}", id);
                    return new ResourceNotFoundException("Producto con id " + id + " no encontrado");
                });
    }

    public boolean existePorId(Long id) {
        return productoRepository.existsById(id);
    }

    public Producto actualizar(Long id, Producto detalles) {
        logger.info("Actualizando producto id={}", id);
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto con id " + id + " no encontrado"));
        producto.setNombre(detalles.getNombre());
        producto.setPrecio(detalles.getPrecio());
        producto.setStock(detalles.getStock());
        if (detalles.getDetalleProducto() != null) {
            if (producto.getDetalleProducto() != null) {
                DetalleProducto detalle = producto.getDetalleProducto();
                DetalleProducto nuevos = detalles.getDetalleProducto();
                detalle.setDescripcion(nuevos.getDescripcion());
                detalle.setContieneLactosa(nuevos.getContieneLactosa());
                detalle.setContieneHuevos(nuevos.getContieneHuevos());
                detalle.setContieneFrutosSecos(nuevos.getContieneFrutosSecos());
                detalle.setContieneGluten(nuevos.getContieneGluten());
                detalle.setAlergenos(nuevos.getAlergenos());
                detalle.setInstruccionesAlmacenamiento(nuevos.getInstruccionesAlmacenamiento());
                detalle.setPresentacion(nuevos.getPresentacion());
            } else {
                producto.setDetalleProducto(detalleProductoRepository.save(detalles.getDetalleProducto()));
            }
        }
        Producto actualizado = productoRepository.save(producto);
        logger.info("Producto actualizado exitosamente id={}", id);
        return actualizado;
    }

    public void eliminar(Long id) {
        logger.info("Eliminando producto id={}", id);
        if (!productoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Producto con id " + id + " no encontrado");
        }
        productoRepository.deleteById(id);
        logger.info("Producto eliminado exitosamente id={}", id);
    }
}
