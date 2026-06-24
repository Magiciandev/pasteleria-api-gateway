package com.example.envio_service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.example.envio_service.model.Envio;
import com.example.envio_service.repository.EnvioRepository;

import java.time.LocalDateTime;

@Component
public class DataLoader implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataLoader.class);

    private final EnvioRepository envioRepository;

    public DataLoader(EnvioRepository envioRepository) {
        this.envioRepository = envioRepository;
    }

    @Override
    public void run(String... args) {
        if (envioRepository.count() > 0) {
            logger.info("DataLoader: ya existen envios, se omite la carga inicial");
            return;
        }

        logger.info("DataLoader: cargando envios iniciales");

        crearEnvio(1L, 1L, "Juan García López", "Avenida Libertador 456, Depto 5A, Santiago",
                "EN_CAMINO", LocalDateTime.of(2026, 5, 15, 10, 30), LocalDateTime.of(2026, 5, 17, 18, 0), null);

        crearEnvio(2L, 2L, "María López Rodríguez", "Pasaje Los Andes 789, Casa 12, Valparaíso",
                "EN_CAMINO", LocalDateTime.of(2026, 5, 14, 14, 45), LocalDateTime.of(2026, 5, 16, 19, 30), null);

        crearEnvio(3L, 3L, "Carlos Rodríguez Martínez", "Camino del Mar 321, Depto 8B, Viña del Mar",
                "ENTREGADO", LocalDateTime.of(2026, 5, 13, 8, 15), LocalDateTime.of(2026, 5, 15, 20, 0),
                LocalDateTime.of(2026, 5, 15, 19, 45));

        crearEnvio(4L, 4L, "Ana Martínez Pérez", "Boulevard Central 654, Casa 23, La Serena",
                "EN_PUNTO_ENTREGA", LocalDateTime.of(2026, 5, 15, 9, 0), LocalDateTime.of(2026, 5, 18, 17, 0), null);

        crearEnvio(5L, 1L, "Roberto Díaz Fernández", "Ruta Interamericana 987, Depto 3C, Puerto Varas",
                "ENTREGADO", LocalDateTime.of(2026, 5, 12, 11, 20), LocalDateTime.of(2026, 5, 14, 21, 0),
                LocalDateTime.of(2026, 5, 14, 20, 30));

        crearEnvio(6L, 2L, "Francisca Torres Acosta", "Paseo del Lago 147, Casa 45, Temuco",
                "EN_CAMINO", LocalDateTime.of(2026, 5, 14, 15, 30), LocalDateTime.of(2026, 5, 16, 22, 0), null);

        crearEnvio(7L, 3L, "Diego Hernández Soto", "Calle Sur 258, Depto 7D, Punta Arenas",
                "EN_CAMINO", LocalDateTime.of(2026, 5, 15, 13, 0), LocalDateTime.of(2026, 5, 22, 16, 0), null);

        crearEnvio(8L, 4L, "Catalina Reyes Gómez", "Avenida Este 369, Casa 67, Iquique",
                "ENTREGADO", LocalDateTime.of(2026, 5, 11, 7, 45), LocalDateTime.of(2026, 5, 13, 18, 30),
                LocalDateTime.of(2026, 5, 13, 18, 15));

        crearEnvio(9L, 1L, "Andrés Flores Rojas", "Camino Costero 741, Depto 2E, Osorno",
                "EN_PUNTO_ENTREGA", LocalDateTime.of(2026, 5, 15, 12, 15), LocalDateTime.of(2026, 5, 17, 19, 0), null);

        crearEnvio(10L, 2L, "Sofía Peña Contreras", "Calle del Comercio 852, Casa 89, Valdivia",
                "ENTREGADO", LocalDateTime.of(2026, 5, 13, 16, 0), LocalDateTime.of(2026, 5, 15, 20, 30),
                LocalDateTime.of(2026, 5, 15, 20, 10));

        logger.info("DataLoader: carga inicial de envios completada, total={}", envioRepository.count());
    }

    private void crearEnvio(Long idPedido, Long idCliente, String nombreCliente, String direccion,
                             String estado, LocalDateTime fechaEnvio, LocalDateTime fechaEstimada,
                             LocalDateTime fechaReal) {
        Envio envio = new Envio(idPedido, idCliente, nombreCliente, direccion, estado,
                fechaEnvio, fechaEstimada, fechaReal);
        envioRepository.save(envio);
    }
}
