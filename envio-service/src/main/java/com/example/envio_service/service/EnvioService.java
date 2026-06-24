package com.example.envio_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.example.envio_service.exception.BadRequestException;
import com.example.envio_service.exception.ExternalServiceException;
import com.example.envio_service.exception.ResourceNotFoundException;
import com.example.envio_service.model.Envio;
import com.example.envio_service.repository.EnvioRepository;
import com.example.envio_service.dto.ClienteDTO;
import com.example.envio_service.dto.PedidoDTO;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EnvioService {

    private static final Logger logger = LoggerFactory.getLogger(EnvioService.class);

    private final EnvioRepository envioRepository;
    private final WebClient.Builder webClientBuilder;
    private static final String CLIENTE_SERVICE_URL = "http://cliente-service:9091";
    private static final String PEDIDO_SERVICE_URL = "http://pedido-service:9094";

    public EnvioService(EnvioRepository envioRepository, WebClient.Builder webClientBuilder) {
        this.envioRepository = envioRepository;
        this.webClientBuilder = webClientBuilder;
    }

    public Envio crearEnvio(Envio envio) {
        logger.info("Iniciando crearEnvio: idPedido={}, idCliente={}", envio.getIdPedido(), envio.getIdCliente());
        if (envio.getIdPedido() == null || envio.getIdCliente() == null) {
            throw new BadRequestException("idPedido e idCliente son requeridos");
        }

        PedidoDTO pedido = obtenerPedidoDelServicio(envio.getIdPedido());
        if (!"CONFIRMADO".equals(pedido.getEstado())) {
            logger.warn("Pedido id={} no está CONFIRMADO. Estado actual: {}", envio.getIdPedido(), pedido.getEstado());
            throw new BadRequestException("El pedido debe estar en estado CONFIRMADO. Estado actual: " + pedido.getEstado());
        }

        ClienteDTO cliente = obtenerClienteDelServicio(envio.getIdCliente());
        envio.setNombreCliente(cliente.getNombre());
        envio.setDireccionEntrega(cliente.getDireccion());
        if (envio.getEstado() == null) envio.setEstado("EN_CAMINO");
        if (envio.getFechaEnvio() == null) envio.setFechaEnvio(LocalDateTime.now());

        Envio guardado = envioRepository.save(envio);
        logger.info("Envio creado exitosamente id={}", guardado.getId());
        return guardado;
    }

    private ClienteDTO obtenerClienteDelServicio(Long idCliente) {
        try {
            ClienteDTO cliente = webClientBuilder.build()
                    .get()
                    .uri(CLIENTE_SERVICE_URL + "/clientes/" + idCliente)
                    .retrieve()
                    .bodyToMono(ClienteDTO.class)
                    .block();
            if (cliente == null) throw new ResourceNotFoundException("Cliente con id " + idCliente + " no encontrado");
            return cliente;
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error al conectar con cliente-service: {}", e.getMessage(), e);
            throw new ExternalServiceException("Error al conectar con cliente-service: " + e.getMessage());
        }
    }

    private PedidoDTO obtenerPedidoDelServicio(Long idPedido) {
        try {
            PedidoDTO pedido = webClientBuilder.build()
                    .get()
                    .uri(PEDIDO_SERVICE_URL + "/pedidos/" + idPedido)
                    .retrieve()
                    .bodyToMono(PedidoDTO.class)
                    .block();
            if (pedido == null) throw new ResourceNotFoundException("Pedido con id " + idPedido + " no encontrado");
            return pedido;
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error al conectar con pedido-service: {}", e.getMessage(), e);
            throw new ExternalServiceException("Error al conectar con pedido-service: " + e.getMessage());
        }
    }

    public List<Envio> listar() {
        logger.info("Listando todos los envios");
        return envioRepository.findAll();
    }

    public Envio buscarPorId(Long id) {
        logger.info("Buscando envio id={}", id);
        return envioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Envio con id " + id + " no encontrado"));
    }

    public List<Envio> buscarPorCliente(Long idCliente) {
        return envioRepository.findByIdCliente(idCliente);
    }

    public Optional<Envio> buscarPorPedido(Long idPedido) {
        return envioRepository.findByIdPedido(idPedido);
    }

    public List<Envio> buscarPorEstado(String estado) {
        return envioRepository.findByEstado(estado);
    }

    public Envio actualizarEstado(Long id, String nuevoEstado) {
        logger.info("Actualizando estado envio id={} -> {}", id, nuevoEstado);
        Envio envio = envioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Envio con id " + id + " no encontrado"));
        envio.setEstado(nuevoEstado);
        if ("ENTREGADO".equals(nuevoEstado)) envio.setFechaEntregaReal(LocalDateTime.now());
        return envioRepository.save(envio);
    }

    public Envio actualizar(Long id, Envio envioDetalles) {
        logger.info("Actualizando envio id={}", id);
        Envio envio = envioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Envio con id " + id + " no encontrado"));
        if (envioDetalles.getDireccionEntrega() != null) envio.setDireccionEntrega(envioDetalles.getDireccionEntrega());
        if (envioDetalles.getFechaEntregaEstimada() != null) envio.setFechaEntregaEstimada(envioDetalles.getFechaEntregaEstimada());
        if (envioDetalles.getEstado() != null) envio.setEstado(envioDetalles.getEstado());
        return envioRepository.save(envio);
    }

    public void eliminar(Long id) {
        logger.info("Eliminando envio id={}", id);
        if (!envioRepository.existsById(id)) throw new ResourceNotFoundException("Envio con id " + id + " no encontrado");
        envioRepository.deleteById(id);
        logger.info("Envio eliminado exitosamente id={}", id);
    }

    public boolean existeEnvio(Long id) {
        return envioRepository.existsById(id);
    }
}
