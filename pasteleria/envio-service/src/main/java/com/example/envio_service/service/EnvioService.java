package com.example.envio_service.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.example.envio_service.model.Envio;
import com.example.envio_service.repository.EnvioRepository;
import com.example.envio_service.dto.ClienteDTO;
import com.example.envio_service.dto.PedidoDTO;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EnvioService {

    private final EnvioRepository envioRepository;
    private final WebClient.Builder webClientBuilder;

    private static final String CLIENTE_SERVICE_URL = "http://localhost:9091";
    private static final String PEDIDO_SERVICE_URL = "http://localhost:9094";
    private static final String ESTADO_PEDIDO_CONFIRMADO = "CONFIRMADO";

    public EnvioService(EnvioRepository envioRepository, WebClient.Builder webClientBuilder) {
        this.envioRepository = envioRepository;
        this.webClientBuilder = webClientBuilder;
    }

    /**
     * Crea un nuevo envío validando que el pedido exista y esté CONFIRMADO.
     * Obtiene datos del cliente desde cliente-service.
     */
    public Envio crearEnvio(Envio envio) {
        if (envio.getIdPedido() == null || envio.getIdCliente() == null) {
            throw new IllegalArgumentException("idPedido e idCliente son requeridos");
        }

        // Validar que el pedido existe y está CONFIRMADO
        PedidoDTO pedido = obtenerPedidoDelServicio(envio.getIdPedido());
        if (pedido == null) {
            throw new IllegalArgumentException("Pedido con ID " + envio.getIdPedido() + " no encontrado");
        }
        if (!ESTADO_PEDIDO_CONFIRMADO.equals(pedido.getEstado())) {
            throw new IllegalArgumentException(
                "El pedido debe estar en estado CONFIRMADO. Estado actual: " + pedido.getEstado()
            );
        }

        // Obtener datos del cliente desde cliente-service
        ClienteDTO cliente = obtenerClienteDelServicio(envio.getIdCliente());
        if (cliente == null) {
            throw new IllegalArgumentException("Cliente con ID " + envio.getIdCliente() + " no encontrado");
        }

        // Asignar datos obtenidos del cliente
        envio.setNombreCliente(cliente.getNombre());
        envio.setDireccionEntrega(cliente.getDireccion());

        // El @PrePersist establecerá el estado por defecto si es null
        if (envio.getEstado() == null) {
            envio.setEstado("EN_CAMINO");
        }

        // El @PrePersist establecerá las fechas por defecto
        if (envio.getFechaEnvio() == null) {
            envio.setFechaEnvio(LocalDateTime.now());
        }

        return envioRepository.save(envio);
    }

    /**
     * Obtiene un cliente desde cliente-service
     */
    private ClienteDTO obtenerClienteDelServicio(Long idCliente) {
        try {
            return webClientBuilder.build()
                .get()
                .uri(CLIENTE_SERVICE_URL + "/clientes/" + idCliente)
                .retrieve()
                .bodyToMono(ClienteDTO.class)
                .block();
        } catch (Exception e) {
            throw new RuntimeException(
                "Error al conectar con cliente-service para obtener cliente con ID " + idCliente + ": " + e.getMessage()
            );
        }
    }

    /**
     * Obtiene un pedido desde pedido-service
     */
    private PedidoDTO obtenerPedidoDelServicio(Long idPedido) {
        try {
            return webClientBuilder.build()
                .get()
                .uri(PEDIDO_SERVICE_URL + "/pedidos/" + idPedido)
                .retrieve()
                .bodyToMono(PedidoDTO.class)
                .block();
        } catch (Exception e) {
            throw new RuntimeException(
                "Error al conectar con pedido-service para obtener pedido con ID " + idPedido + ": " + e.getMessage()
            );
        }
    }

    /**
     * Lista todos los envíos
     */
    public List<Envio> listar() {
        return envioRepository.findAll();
    }

    /**
     * Obtiene un envío por ID
     */
    public Envio buscarPorId(Long id) {
        return envioRepository.findById(id).orElse(null);
    }

    /**
     * Obtiene envíos por cliente
     */
    public List<Envio> buscarPorCliente(Long idCliente) {
        return envioRepository.findByIdCliente(idCliente);
    }

    /**
     * Obtiene un envío por pedido
     */
    public Optional<Envio> buscarPorPedido(Long idPedido) {
        return envioRepository.findByIdPedido(idPedido);
    }

    /**
     * Obtiene envíos por estado
     */
    public List<Envio> buscarPorEstado(String estado) {
        return envioRepository.findByEstado(estado);
    }

    /**
     * Actualiza solo el estado de un envío
     */
    public Envio actualizarEstado(Long id, String nuevoEstado) {
        Envio envio = envioRepository.findById(id).orElse(null);
        if (envio != null) {
            envio.setEstado(nuevoEstado);
            if ("ENTREGADO".equals(nuevoEstado)) {
                envio.setFechaEntregaReal(LocalDateTime.now());
            }
            return envioRepository.save(envio);
        }
        return null;
    }

    /**
     * Actualiza los detalles de un envío existente
     */
    public Envio actualizar(Long id, Envio envioDetalles) {
        Envio envio = envioRepository.findById(id).orElse(null);
        if (envio != null) {
            if (envioDetalles.getDireccionEntrega() != null) {
                envio.setDireccionEntrega(envioDetalles.getDireccionEntrega());
            }
            if (envioDetalles.getFechaEntregaEstimada() != null) {
                envio.setFechaEntregaEstimada(envioDetalles.getFechaEntregaEstimada());
            }
            if (envioDetalles.getEstado() != null) {
                envio.setEstado(envioDetalles.getEstado());
            }
            return envioRepository.save(envio);
        }
        return null;
    }

    /**
     * Elimina un envío
     */
    public void eliminar(Long id) {
        envioRepository.deleteById(id);
    }

    /**
     * Verifica si existe un envío con el ID especificado
     */
    public boolean existeEnvio(Long id) {
        return envioRepository.existsById(id);
    }
}
