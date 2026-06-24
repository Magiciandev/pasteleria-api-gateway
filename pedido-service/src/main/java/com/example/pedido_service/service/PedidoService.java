package com.example.pedido_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.example.pedido_service.exception.BadRequestException;
import com.example.pedido_service.exception.ExternalServiceException;
import com.example.pedido_service.exception.ResourceNotFoundException;
import com.example.pedido_service.model.DetallePedido;
import com.example.pedido_service.model.Pedido;
import com.example.pedido_service.repository.PedidoRepository;
import com.example.pedido_service.dto.ClienteDTO;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PedidoService {

    private static final Logger logger = LoggerFactory.getLogger(PedidoService.class);

    private final PedidoRepository pedidoRepository;
    private final WebClient.Builder webClientBuilder;

    public PedidoService(PedidoRepository pedidoRepository, WebClient.Builder webClientBuilder) {
        this.pedidoRepository = pedidoRepository;
        this.webClientBuilder = webClientBuilder;
    }

    public Pedido guardar(Pedido pedido) {
        logger.info("Iniciando guardar pedido: idCliente={}", pedido.getIdCliente());
        try {
            ClienteDTO cliente = webClientBuilder.build()
                    .get()
                    .uri("http://cliente-service:9091/clientes/" + pedido.getIdCliente())
                    .retrieve()
                    .bodyToMono(ClienteDTO.class)
                    .block();

            if (cliente == null) {
                logger.warn("Cliente no encontrado id={}", pedido.getIdCliente());
                throw new BadRequestException("Cliente con id " + pedido.getIdCliente() + " no existe");
            }
            pedido.setNombreCliente(cliente.getNombre());
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error al conectar con cliente-service: {}", e.getMessage(), e);
            throw new ExternalServiceException("Error al conectar con cliente-service: " + e.getMessage());
        }

        if (pedido.getDetalles() == null || pedido.getDetalles().isEmpty()) {
            throw new BadRequestException("El pedido debe tener al menos un detalle");
        }

        for (DetallePedido detalle : pedido.getDetalles()) {
            detalle.setPedido(pedido);
        }

        double totalCalculado = pedido.getDetalles().stream()
                .mapToDouble(DetallePedido::getPrecioTotal)
                .sum();
        pedido.setTotal(totalCalculado);

        if (pedido.getFecha() == null) pedido.setFecha(LocalDateTime.now());
        if (pedido.getEstado() == null) pedido.setEstado("PENDIENTE");

        Pedido guardado = pedidoRepository.save(pedido);
        logger.info("Pedido guardado exitosamente id={}, total={}", guardado.getId(), guardado.getTotal());
        return guardado;
    }

    public List<Pedido> listar() {
        logger.info("Listando todos los pedidos");
        List<Pedido> pedidos = pedidoRepository.findAll();
        logger.info("Total pedidos: {}", pedidos.size());
        return pedidos;
    }

    public Pedido buscarPorId(Long id) {
        logger.info("Buscando pedido id={}", id);
        return pedidoRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Pedido no encontrado id={}", id);
                    return new ResourceNotFoundException("Pedido con id " + id + " no encontrado");
                });
    }

    public List<Pedido> buscarPorIdCliente(Long idCliente) {
        return pedidoRepository.findByIdCliente(idCliente);
    }

    public List<Pedido> buscarPorRangoFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return pedidoRepository.findByFechaBetween(fechaInicio, fechaFin);
    }

    public Pedido actualizar(Long id, Pedido detalles) {
        logger.info("Actualizando pedido id={}", id);
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido con id " + id + " no encontrado"));
        if (detalles.getEstado() != null) pedido.setEstado(detalles.getEstado());
        if (detalles.getDetalles() != null && !detalles.getDetalles().isEmpty()) {
            pedido.getDetalles().clear();
            for (DetallePedido detalle : detalles.getDetalles()) {
                pedido.agregarDetalle(detalle);
            }
            double totalCalculado = pedido.getDetalles().stream()
                    .mapToDouble(DetallePedido::getPrecioTotal)
                    .sum();
            pedido.setTotal(totalCalculado);
        }
        Pedido actualizado = pedidoRepository.save(pedido);
        logger.info("Pedido actualizado exitosamente id={}", id);
        return actualizado;
    }

    public void eliminar(Long id) {
        logger.info("Eliminando pedido id={}", id);
        if (!pedidoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Pedido con id " + id + " no encontrado");
        }
        pedidoRepository.deleteById(id);
        logger.info("Pedido eliminado exitosamente id={}", id);
    }
}
