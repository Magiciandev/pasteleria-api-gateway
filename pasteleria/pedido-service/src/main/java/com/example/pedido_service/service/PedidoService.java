package com.example.pedido_service.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.example.pedido_service.model.Pedido;
import com.example.pedido_service.repository.PedidoRepository;
import com.example.pedido_service.dto.ClienteDTO;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final WebClient.Builder webClientBuilder;

    public PedidoService(PedidoRepository pedidoRepository, WebClient.Builder webClientBuilder) {
        this.pedidoRepository = pedidoRepository;
        this.webClientBuilder = webClientBuilder;
    }

    public Pedido guardar(Pedido pedido) {
        ClienteDTO cliente = null;
        try {
            cliente = webClientBuilder.build()
                    .get()
                    .uri("http://localhost:9091/clientes/" + pedido.getIdCliente())
                    .retrieve()
                    .bodyToMono(ClienteDTO.class)
                    .block();

            if (cliente == null) {
                throw new IllegalArgumentException("[ERROR] CLIENTE NO EXISTE");
            }
            
            pedido.setNombreCliente(cliente.getNombre());
        } catch (Exception e) {
            throw new IllegalArgumentException("[ERROR] CLIENTE NO EXISTE");
        }

        if (pedido.getFecha() == null) {
            pedido.setFecha(LocalDateTime.now());
        }
        
        if (pedido.getEstado() == null) {
            pedido.setEstado("PENDIENTE");
        }

        if (pedido.getDetallePedido() == null) {
            pedido.setDetallePedido("[]");
        }

        if (pedido.getTotal() == null) {
            pedido.setTotal(0.0);
        }

        return pedidoRepository.save(pedido);
    }

    public List<Pedido> listar() {
        return pedidoRepository.findAll();
    }

    public Pedido buscarPorId(Long id) {
        return pedidoRepository.findById(id).orElse(null);
    }

    public List<Pedido> buscarPorIdCliente(Long idCliente) {
        return pedidoRepository.findByIdCliente(idCliente);
    }

    public List<Pedido> buscarPorRangoFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return pedidoRepository.findByFechaBetween(fechaInicio, fechaFin);
    }

    public Pedido actualizar(Long id, Pedido detalles) {
        Pedido pedido = pedidoRepository.findById(id).orElse(null);
        if (pedido != null) {
            if (detalles.getEstado() != null) {
                pedido.setEstado(detalles.getEstado());
            }
            if (detalles.getDetallePedido() != null) {
                pedido.setDetallePedido(detalles.getDetallePedido());
            }
            if (detalles.getTotal() != null) {
                pedido.setTotal(detalles.getTotal());
            }
            return pedidoRepository.save(pedido);
        }
        return null;
    }

    public void eliminar(Long id) {
        pedidoRepository.deleteById(id);
    }
}
