package com.example.envio_service;

import com.example.envio_service.dto.ClienteDTO;
import com.example.envio_service.dto.PedidoDTO;
import com.example.envio_service.model.Envio;
import com.example.envio_service.repository.EnvioRepository;
import com.example.envio_service.service.EnvioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driverClassName=org.h2.Driver",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
public class EnvioServiceApplicationTest {

    @Autowired
    private EnvioService envioService;

    @Autowired
    private EnvioRepository envioRepository;

    private Envio envioTest;

    @BeforeEach
    public void setUp() {
        envioRepository.deleteAll();
        
        envioTest = new Envio();
        envioTest.setIdPedido(1L);
        envioTest.setIdCliente(1L);
        envioTest.setNombreCliente("Juan García López");
        envioTest.setDireccionEntrega("Avenida Libertador 456, Santiago");
        envioTest.setEstado("EN_CAMINO");
        envioTest.setFechaEnvio(LocalDateTime.now());
        envioTest.setFechaEntregaEstimada(LocalDateTime.now().plusDays(2));
    }

    @Test
    public void testContextLoads() {
        assertNotNull(envioService);
        assertNotNull(envioRepository);
    }

    @Test
    public void testGuardarEnvio() {
        Envio enviado = envioRepository.save(envioTest);
        assertNotNull(enviado.getId());
        assertEquals("EN_CAMINO", enviado.getEstado());
        assertEquals("Juan García López", enviado.getNombreCliente());
    }

    @Test
    public void testBuscarEnvioPorId() {
        Envio enviado = envioRepository.save(envioTest);
        Optional<Envio> encontrado = envioRepository.findById(enviado.getId());
        assertTrue(encontrado.isPresent());
        assertEquals(enviado.getId(), encontrado.get().getId());
    }

    @Test
    public void testBuscarEnvioPorCliente() {
        envioRepository.save(envioTest);
        
        Envio envio2 = new Envio();
        envio2.setIdPedido(2L);
        envio2.setIdCliente(2L);
        envio2.setNombreCliente("María López");
        envio2.setDireccionEntrega("Calle Principal 123");
        envio2.setEstado("EN_CAMINO");
        envioRepository.save(envio2);
        
        var enviosPorCliente1 = envioRepository.findByIdCliente(1L);
        assertEquals(1, enviosPorCliente1.size());
        assertEquals("Juan García López", enviosPorCliente1.get(0).getNombreCliente());
    }

    @Test
    public void testBuscarEnvioPorPedido() {
        Envio enviado = envioRepository.save(envioTest);
        Optional<Envio> encontrado = envioRepository.findByIdPedido(1L);
        assertTrue(encontrado.isPresent());
        assertEquals(enviado.getId(), encontrado.get().getId());
    }

    @Test
    public void testBuscarEnvioPorEstado() {
        envioRepository.save(envioTest);
        
        Envio envio2 = new Envio();
        envio2.setIdPedido(2L);
        envio2.setIdCliente(2L);
        envio2.setNombreCliente("María López");
        envio2.setDireccionEntrega("Calle Principal 123");
        envio2.setEstado("ENTREGADO");
        envioRepository.save(envio2);
        
        var enviosEnCamino = envioRepository.findByEstado("EN_CAMINO");
        assertEquals(1, enviosEnCamino.size());
        
        var enviosEntregados = envioRepository.findByEstado("ENTREGADO");
        assertEquals(1, enviosEntregados.size());
    }

    @Test
    public void testActualizarEnvio() {
        Envio enviado = envioRepository.save(envioTest);
        enviado.setEstado("EN_PUNTO_ENTREGA");
        Envio actualizado = envioRepository.save(enviado);
        
        Optional<Envio> encontrado = envioRepository.findById(actualizado.getId());
        assertTrue(encontrado.isPresent());
        assertEquals("EN_PUNTO_ENTREGA", encontrado.get().getEstado());
    }

    @Test
    public void testEliminarEnvio() {
        Envio enviado = envioRepository.save(envioTest);
        Long id = enviado.getId();
        
        envioRepository.deleteById(id);
        Optional<Envio> encontrado = envioRepository.findById(id);
        assertFalse(encontrado.isPresent());
    }

    @Test
    public void testVerificarEstadoDefaultEnPersist() {
        Envio envio = new Envio();
        envio.setIdPedido(3L);
        envio.setIdCliente(3L);
        envio.setNombreCliente("Carlos");
        envio.setDireccionEntrega("Calle del Mar");
        // No establecemos estado
        
        Envio guardado = envioRepository.save(envio);
        assertEquals("EN_CAMINO", guardado.getEstado());
    }

    @Test
    public void testVerificarFechasEnPersist() {
        Envio envio = new Envio();
        envio.setIdPedido(4L);
        envio.setIdCliente(4L);
        envio.setNombreCliente("Ana");
        envio.setDireccionEntrega("Avenida Central");
        
        Envio guardado = envioRepository.save(envio);
        assertNotNull(guardado.getCreatedAt());
        assertNotNull(guardado.getUpdatedAt());
        assertNotNull(guardado.getFechaEnvio());
    }
}
