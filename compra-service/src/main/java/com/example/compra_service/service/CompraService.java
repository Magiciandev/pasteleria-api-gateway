package com.example.compra_service.service;

import com.example.compra_service.dto.MovimientoInventarioDTO;
import com.example.compra_service.dto.ProveedorDTO;
import com.example.compra_service.exception.BadRequestException;
import com.example.compra_service.exception.ExternalServiceException;
import com.example.compra_service.exception.ResourceNotFoundException;
import com.example.compra_service.model.Compra;
import com.example.compra_service.model.DetalleCompra;
import com.example.compra_service.repository.CompraRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CompraService {

    private static final Logger logger = LoggerFactory.getLogger(CompraService.class);

    private final CompraRepository compraRepository;
    private final WebClient.Builder webClientBuilder;

    @Value("${servicios.proveedor.url}")
    private String proveedorServiceUrl;

    @Value("${servicios.inventario.url}")
    private String inventarioServiceUrl;

    public CompraService(CompraRepository compraRepository, WebClient.Builder webClientBuilder) {
        this.compraRepository = compraRepository;
        this.webClientBuilder = webClientBuilder;
    }

    /**
     * Crea una orden de compra. Valida contra proveedor-service que el proveedor
     * exista y esté activo antes de aceptar la compra.
     */
    public Compra guardar(Compra compra) {
        logger.info("Iniciando guardar compra: idProveedor={}, numeroFactura={}",
                compra.getIdProveedor(), compra.getNumeroFactura());

        ProveedorDTO proveedor = obtenerProveedor(compra.getIdProveedor());
        if (Boolean.FALSE.equals(proveedor.getActivo())) {
            throw new BadRequestException(
                    "El proveedor " + proveedor.getNombre() + " no está activo y no puede recibir órdenes de compra");
        }
        compra.setNombreProveedor(proveedor.getNombre());

        if (compra.getDetalles() == null || compra.getDetalles().isEmpty()) {
            throw new BadRequestException("La compra debe tener al menos un detalle");
        }

        for (DetalleCompra detalle : compra.getDetalles()) {
            detalle.setCompra(compra);
        }

        double totalCalculado = compra.getDetalles().stream()
                .mapToDouble(DetalleCompra::getPrecioTotal)
                .sum();
        compra.setMontoTotal(totalCalculado);

        if (compra.getFecha() == null) compra.setFecha(LocalDateTime.now());
        if (compra.getEstado() == null) compra.setEstado("PENDIENTE");

        Compra guardada = compraRepository.save(compra);
        logger.info("Compra guardada exitosamente id={}, montoTotal={}", guardada.getId(), guardada.getMontoTotal());
        return guardada;
    }

    public List<Compra> listar() {
        logger.info("Listando todas las compras");
        List<Compra> compras = compraRepository.findAll();
        logger.info("Total compras: {}", compras.size());
        return compras;
    }

    public Compra buscarPorId(Long id) {
        logger.info("Buscando compra id={}", id);
        return compraRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Compra no encontrada id={}", id);
                    return new ResourceNotFoundException("Compra con id " + id + " no encontrada");
                });
    }

    public List<Compra> buscarPorProveedor(Long idProveedor) {
        return compraRepository.findByIdProveedor(idProveedor);
    }

    public List<Compra> buscarPorEstado(String estado) {
        return compraRepository.findByEstado(estado);
    }

    /**
     * Marca una compra como RECIBIDA y, como consecuencia, registra una
     * ENTRADA de inventario por cada línea de detalle en inventario-service.
     * Regla de negocio: una compra ANULADA no puede recibirse, y una compra
     * ya RECIBIDA no puede volver a cargar stock (evita duplicar entradas).
     */
    public Compra recibir(Long id) {
        logger.info("Recibiendo compra id={}", id);
        Compra compra = compraRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Compra con id " + id + " no encontrada"));

        if ("ANULADA".equalsIgnoreCase(compra.getEstado())) {
            throw new BadRequestException("No se puede recibir una compra que está ANULADA");
        }
        if ("RECIBIDA".equalsIgnoreCase(compra.getEstado())) {
            throw new BadRequestException("La compra ya fue recibida anteriormente; no se puede cargar el stock dos veces");
        }

        for (DetalleCompra detalle : compra.getDetalles()) {
            registrarEntradaInventario(compra, detalle);
        }

        compra.setEstado("RECIBIDA");
        Compra actualizada = compraRepository.save(compra);
        logger.info("Compra recibida exitosamente id={}, stock cargado en inventario-service", id);
        return actualizada;
    }

    /**
     * Anula una compra. Regla de negocio: una compra ya RECIBIDA no puede
     * anularse, ya que el stock correspondiente ya fue cargado a inventario.
     */
    public Compra anular(Long id) {
        logger.info("Anulando compra id={}", id);
        Compra compra = compraRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Compra con id " + id + " no encontrada"));

        if ("RECIBIDA".equalsIgnoreCase(compra.getEstado())) {
            throw new BadRequestException("No se puede anular una compra que ya fue RECIBIDA");
        }

        compra.setEstado("ANULADA");
        Compra actualizada = compraRepository.save(compra);
        logger.info("Compra anulada exitosamente id={}", id);
        return actualizada;
    }

    public void eliminar(Long id) {
        logger.info("Eliminando compra id={}", id);
        if (!compraRepository.existsById(id)) {
            throw new ResourceNotFoundException("Compra con id " + id + " no encontrada");
        }
        compraRepository.deleteById(id);
        logger.info("Compra eliminada exitosamente id={}", id);
    }

    /**
     * Consulta el proveedor asociado vía WebClient hacia proveedor-service.
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

    /**
     * Registra una ENTRADA de inventario vía WebClient hacia inventario-service
     * por cada línea de detalle de la compra recibida.
     */
    private void registrarEntradaInventario(Compra compra, DetalleCompra detalle) {
        MovimientoInventarioDTO movimiento = MovimientoInventarioDTO.builder()
                .idProducto(detalle.getIdProducto())
                .nombreProducto(detalle.getNombreProducto())
                .tipo("ENTRADA")
                .cantidad(detalle.getCantidad())
                // Simplificación de alcance académico: compra-service no tiene visibilidad
                // del stock actual de inventario-service, por lo que se reporta la cantidad
                // recibida en esta compra como stock resultante de este movimiento puntual.
                .stockResultante(detalle.getCantidad())
                .idProveedor(compra.getIdProveedor())
                .motivo("Recepción de compra N° factura " + compra.getNumeroFactura())
                .fecha(LocalDateTime.now())
                .build();

        try {
            webClientBuilder.build()
                    .post()
                    .uri(inventarioServiceUrl + "/inventario")
                    .bodyValue(movimiento)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (Exception e) {
            logger.error("Error al conectar con inventario-service: {}", e.getMessage(), e);
            throw new ExternalServiceException("Error al conectar con inventario-service: " + e.getMessage());
        }
    }
}
