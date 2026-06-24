package com.example.cliente_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.example.cliente_service.exception.BadRequestException;
import com.example.cliente_service.exception.ExternalServiceException;
import com.example.cliente_service.exception.ResourceNotFoundException;
import com.example.cliente_service.model.Cliente;
import com.example.cliente_service.repository.ClienteRepository;
import java.util.List;

@Service
public class ClienteService {

    private static final Logger logger = LoggerFactory.getLogger(ClienteService.class);

    private final ClienteRepository clienteRepository;
    private final WebClient.Builder webClientBuilder;

    public ClienteService(ClienteRepository clienteRepository, WebClient.Builder webClientBuilder) {
        this.clienteRepository = clienteRepository;
        this.webClientBuilder = webClientBuilder;
    }

    public Cliente guardar(Cliente cliente) {
        logger.info("Iniciando guardar cliente: correo={}", cliente.getCorreo());
        try {
            Boolean usuarioExiste = webClientBuilder.build()
                    .get()
                    .uri("http://auth-service:9097/auth/user/" + cliente.getCorreo() + "/exists")
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block();

            if (usuarioExiste == null) {
                logger.error("No se pudo validar existencia del usuario en auth-service");
                throw new ExternalServiceException("No se pudo validar el usuario en auth-service");
            }
            if (Boolean.FALSE.equals(usuarioExiste)) {
                logger.warn("Usuario no existe en auth-service: correo={}", cliente.getCorreo());
                throw new BadRequestException("No existe un usuario registrado con el correo: " + cliente.getCorreo());
            }

            Cliente guardado = clienteRepository.save(cliente);
            logger.info("Cliente guardado exitosamente id={}", guardado.getId());
            return guardado;
        } catch (BadRequestException | ExternalServiceException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error al conectar con auth-service: {}", e.getMessage(), e);
            throw new ExternalServiceException("Error al conectar con auth-service: " + e.getMessage());
        }
    }

    public List<Cliente> listar() {
        logger.info("Listando todos los clientes");
        List<Cliente> clientes = clienteRepository.findAll();
        logger.info("Total clientes: {}", clientes.size());
        return clientes;
    }

    public Cliente buscarPorId(Long id) {
        logger.info("Buscando cliente por id={}", id);
        return clienteRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Cliente no encontrado id={}", id);
                    return new ResourceNotFoundException("Cliente con id " + id + " no encontrado");
                });
    }

    public Cliente buscarPorRut(String rut) {
        logger.info("Buscando cliente por rut={}", rut);
        return clienteRepository.findByRutCliente(rut)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente con rut " + rut + " no encontrado"));
    }

    public Cliente buscarPorCorreo(String correo) {
        logger.info("Buscando cliente por correo={}", correo);
        return clienteRepository.findByCorreo(correo)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente con correo " + correo + " no encontrado"));
    }

    public List<Cliente> buscarPorNombre(String nombre) {
        logger.info("Buscando clientes por nombre={}", nombre);
        return clienteRepository.findByNombreContainingIgnoreCase(nombre);
    }

    public boolean existePorId(Long id) {
        return clienteRepository.existsById(id);
    }

    public Cliente actualizar(Long id, Cliente clienteDetalles) {
        logger.info("Actualizando cliente id={}", id);
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente con id " + id + " no encontrado"));
        cliente.setRutCliente(clienteDetalles.getRutCliente());
        cliente.setNombre(clienteDetalles.getNombre());
        cliente.setCorreo(clienteDetalles.getCorreo());
        cliente.setTelefono(clienteDetalles.getTelefono());
        cliente.setDireccion(clienteDetalles.getDireccion());
        Cliente actualizado = clienteRepository.save(cliente);
        logger.info("Cliente actualizado exitosamente id={}", id);
        return actualizado;
    }

    public void eliminar(Long id) {
        logger.info("Eliminando cliente id={}", id);
        if (!clienteRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cliente con id " + id + " no encontrado");
        }
        clienteRepository.deleteById(id);
        logger.info("Cliente eliminado exitosamente id={}", id);
    }
}
