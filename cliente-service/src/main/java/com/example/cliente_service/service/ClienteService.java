package com.example.cliente_service.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.example.cliente_service.model.Cliente;
import com.example.cliente_service.repository.ClienteRepository;
import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final WebClient.Builder webClientBuilder;

    public ClienteService(ClienteRepository clienteRepository, WebClient.Builder webClientBuilder) {
        this.clienteRepository = clienteRepository;
        this.webClientBuilder = webClientBuilder;
    }

    public Cliente guardar(Cliente cliente) {
        // Comunicación con auth-service: Verificar si existe usuario con el correo
        Boolean usuarioExiste = webClientBuilder.build()
                .get()
                .uri("http://localhost:9094/auth/user/" + cliente.getCorreo() + "/exists")
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();

        if (Boolean.FALSE.equals(usuarioExiste)) {
            throw new IllegalArgumentException("[ERROR] USUARIO NO EXISTE EN AUTH-SERVICE");
        }

        return clienteRepository.save(cliente);
    }

    public List<Cliente> listar() {
        return clienteRepository.findAll();
    }

    public Cliente buscarPorId(Long id) {
        return clienteRepository.findById(id).orElse(null);
    }

    public Cliente buscarPorRut(String rut) {
        return clienteRepository.findByRutCliente(rut).orElse(null);
    }

    public Cliente buscarPorCorreo(String correo) {
        return clienteRepository.findByCorreo(correo).orElse(null);
    }

    public List<Cliente> buscarPorNombre(String nombre) {
        return clienteRepository.findByNombreContainingIgnoreCase(nombre);
    }

    public boolean existePorId(Long id) {
        return clienteRepository.existsById(id);
    }

    public Cliente actualizar(Long id, Cliente clienteDetalles) {
        Cliente cliente = clienteRepository.findById(id).orElse(null);
        if (cliente != null) {
            cliente.setRutCliente(clienteDetalles.getRutCliente());
            cliente.setNombre(clienteDetalles.getNombre());
            cliente.setCorreo(clienteDetalles.getCorreo());
            cliente.setTelefono(clienteDetalles.getTelefono());
            cliente.setDireccion(clienteDetalles.getDireccion());
            return clienteRepository.save(cliente);
        }
        return null;
    }

    public void eliminar(Long id) {
        clienteRepository.deleteById(id);
    }
}