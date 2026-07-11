package com.example.inventario_service.service;

import com.example.inventario_service.dto.ProveedorDTO;
import com.example.inventario_service.exception.BadRequestException;
import com.example.inventario_service.exception.ExternalServiceException;
import com.example.inventario_service.exception.ResourceNotFoundException;
import com.example.inventario_service.model.MovimientoInventario;
import com.example.inventario_service.repository.MovimientoInventarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;

@Service
public class MovimientoInventarioService {

    private static final Logger logger = LoggerFactory.getLogger(MovimientoInventarioService.class);

    /**
     * Umbral mínimo de stock: si una SALIDA deja el stock resultante por debajo
     * de este valor, se considera que el producto necesita reposición y el
     * servicio valida el estado del proveedor asociado.
     */
    private static final int STOCK_MINIMO = 10;

    private final MovimientoInventarioRepository movimientoRepository;
    private final WebClient.Builder webClientBuilder;

    @Value("${servicios.proveedor.url}")
    private String proveedorServiceUrl;

    public MovimientoInventarioService(MovimientoInventarioRepository movimientoRepository,
                                        WebClient.Builder webClientBuilder) {
        this.movimientoRepository = movimientoRepository;
        this.webClientBuilder = webClientBuilder;
    }

    public MovimientoInventario registrar(MovimientoInventario movimiento) {
        logger.info("Registrando movimiento de inventario: idProducto={}, tipo={}, cantidad={}",
                movimiento.getIdProducto(), movimiento.getTipo(), movimiento.getCantidad());

        validarTipo(movimiento.getTipo());

        if (movimiento.getStockResultante() == null || movimiento.getStockResultante() < 0) {
            throw new BadRequestException("El stock resultante no puede ser negativo");
        }

        // Regla de negocio: si es una SALIDA y el stock queda bajo el mínimo,
        // se valida que el proveedor asociado exista y esté activo antes de aceptar el movimiento.
        if ("SALIDA".equalsIgnoreCase(movimiento.getTipo()) && movimiento.getStockResultante() < STOCK_MINIMO) {
            if (movimiento.getIdProveedor() == null) {
                throw new BadRequestException(
                        "El stock quedará bajo el mínimo (" + STOCK_MINIMO + "); debe indicar el proveedor a notificar para reposición");
            }
            ProveedorDTO proveedor = obtenerProveedor(movimiento.getIdProveedor());
            if (Boolean.FALSE.equals(proveedor.getActivo())) {
                throw new BadRequestException(
                        "El proveedor " + proveedor.getNombre() + " no está activo y no puede surtir este producto");
            }
            logger.info("Stock bajo el mínimo para producto id={}; proveedor {} notificado para reposición",
                    movimiento.getIdProducto(), proveedor.getNombre());
        }

        MovimientoInventario guardado = movimientoRepository.save(movimiento);
        logger.info("Movimiento de inventario registrado exitosamente id={}", guardado.getId());
        return guardado;
    }

    public List<MovimientoInventario> listar() {
        logger.info("Listando todos los movimientos de inventario");
        List<MovimientoInventario> movimientos = movimientoRepository.findAll();
        logger.info("Total movimientos: {}", movimientos.size());
        return movimientos;
    }

    public List<MovimientoInventario> listarPorProducto(Long idProducto) {
        logger.info("Listando movimientos del producto id={}", idProducto);
        return movimientoRepository.findByIdProductoOrderByFechaDesc(idProducto);
    }

    public List<MovimientoInventario> listarPorTipo(String tipo) {
        logger.info("Listando movimientos de tipo={}", tipo);
        validarTipo(tipo);
        return movimientoRepository.findByTipo(tipo.toUpperCase());
    }

    public MovimientoInventario buscarPorId(Long id) {
        logger.info("Buscando movimiento de inventario id={}", id);
        return movimientoRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Movimiento de inventario no encontrado id={}", id);
                    return new ResourceNotFoundException("Movimiento de inventario con id " + id + " no encontrado");
                });
    }

    public MovimientoInventario actualizar(Long id, MovimientoInventario detalles) {
        logger.info("Actualizando movimiento de inventario id={}", id);
        MovimientoInventario movimiento = movimientoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movimiento de inventario con id " + id + " no encontrado"));

        validarTipo(detalles.getTipo());

        movimiento.setIdProducto(detalles.getIdProducto());
        movimiento.setNombreProducto(detalles.getNombreProducto());
        movimiento.setTipo(detalles.getTipo());
        movimiento.setCantidad(detalles.getCantidad());
        movimiento.setStockResultante(detalles.getStockResultante());
        movimiento.setIdProveedor(detalles.getIdProveedor());
        movimiento.setMotivo(detalles.getMotivo());

        MovimientoInventario actualizado = movimientoRepository.save(movimiento);
        logger.info("Movimiento de inventario actualizado exitosamente id={}", id);
        return actualizado;
    }

    public void eliminar(Long id) {
        logger.info("Eliminando movimiento de inventario id={}", id);
        if (!movimientoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Movimiento de inventario con id " + id + " no encontrado");
        }
        movimientoRepository.deleteById(id);
        logger.info("Movimiento de inventario eliminado exitosamente id={}", id);
    }

    /**
     * Consulta el proveedor asociado vía WebClient (comunicación síncrona REST
     * hacia proveedor-service), replicando el patrón usado en pedido-service
     * hacia cliente-service.
     */
    private ProveedorDTO obtenerProveedor(Long idProveedor) {
        try {
            ProveedorDTO proveedor = webClientBuilder.build()
                    .get()
                    .uri(proveedorServiceUrl + "/proveedores/" + idProveedor)
                    .retrieve()
                    .bodyToMono(ProveedorDTO.class)
                    .block();

            if (proveedor == null) {
                throw new ResourceNotFoundException("Proveedor con id " + idProveedor + " no existe");
            }
            return proveedor;
        } catch (WebClientResponseException.NotFound e) {
            logger.warn("Proveedor no encontrado id={}", idProveedor);
            throw new ResourceNotFoundException("Proveedor con id " + idProveedor + " no existe");
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error al conectar con proveedor-service: {}", e.getMessage(), e);
            throw new ExternalServiceException("Error al conectar con proveedor-service: " + e.getMessage());
        }
    }

    private void validarTipo(String tipo) {
        if (tipo == null || !List.of("ENTRADA", "SALIDA", "RESERVA", "AJUSTE").contains(tipo.toUpperCase())) {
            throw new BadRequestException("El tipo de movimiento debe ser ENTRADA, SALIDA, RESERVA o AJUSTE");
        }
    }
}
