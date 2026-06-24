package com.example.asistencia_service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.example.asistencia_service.model.Asistencia;
import com.example.asistencia_service.repository.AsistenciaRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Component
public class DataLoader implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataLoader.class);

    private final AsistenciaRepository asistenciaRepository;

    public DataLoader(AsistenciaRepository asistenciaRepository) {
        this.asistenciaRepository = asistenciaRepository;
    }

    @Override
    public void run(String... args) {
        if (asistenciaRepository.count() > 0) {
            logger.info("DataLoader: ya existen asistencias, se omite la carga inicial");
            return;
        }

        logger.info("DataLoader: cargando asistencias iniciales");

        crearAsistencia(1L, "Juan Pérez González", LocalDate.of(2025, 6, 1),
                LocalTime.of(8, 0), LocalTime.of(17, 30), "9.50", "0.50", "7500.00", true, "Trabajó normalmente");

        crearAsistencia(2L, "María García López", LocalDate.of(2025, 6, 1),
                LocalTime.of(8, 15), LocalTime.of(17, 45), "9.50", "0.50", "7750.00", true, "Entrada retrasada");

        crearAsistencia(3L, "Carlos Rodríguez Martín", LocalDate.of(2025, 6, 2),
                LocalTime.of(8, 0), LocalTime.of(18, 0), "10.00", "1.00", "12000.00", true, "Trabajó hora extra");

        crearAsistencia(4L, "Rosa Martínez Flores", LocalDate.of(2025, 6, 2),
                LocalTime.of(8, 0), LocalTime.of(17, 0), "9.00", "0.00", "0.00", true, "Jornada normal");

        crearAsistencia(5L, "Roberto López Silva", LocalDate.of(2025, 6, 3),
                LocalTime.of(7, 30), LocalTime.of(17, 30), "10.00", "1.00", "17000.00", true, "Entrada temprana");

        crearAsistencia(1L, "Juan Pérez González", LocalDate.of(2025, 6, 4),
                LocalTime.of(8, 0), LocalTime.of(16, 30), "8.50", "0.00", "0.00", false, "No asistió");

        crearAsistencia(2L, "María García López", LocalDate.of(2025, 6, 4),
                LocalTime.of(8, 0), LocalTime.of(18, 30), "10.50", "1.50", "11625.00", true, "Trabajó 10.5 horas");

        crearAsistencia(3L, "Carlos Rodríguez Martín", LocalDate.of(2025, 6, 5),
                LocalTime.of(8, 0), LocalTime.of(17, 0), "9.00", "0.00", "0.00", true, "Jornada normal");

        crearAsistencia(4L, "Rosa Martínez Flores", LocalDate.of(2025, 6, 5),
                LocalTime.of(8, 0), LocalTime.of(18, 15), "10.25", "1.25", "13750.00", true, "Trabajó horas extra");

        crearAsistencia(5L, "Roberto López Silva", LocalDate.of(2025, 6, 6),
                LocalTime.of(8, 0), LocalTime.of(17, 30), "9.50", "0.50", "8500.00", true, "Semana completa");

        logger.info("DataLoader: carga inicial de asistencias completada, total={}", asistenciaRepository.count());
    }

    private void crearAsistencia(Long idEmpleado, String nombreEmpleado, LocalDate fecha,
                                  LocalTime horaEntrada, LocalTime horaSalida,
                                  String horasTrabajadas, String horasExtra, String costoHorasExtra,
                                  boolean presente, String observacion) {
        Asistencia asistencia = new Asistencia();
        asistencia.setIdEmpleado(idEmpleado);
        asistencia.setNombreEmpleado(nombreEmpleado);
        asistencia.setFecha(fecha);
        asistencia.setHoraEntrada(horaEntrada);
        asistencia.setHoraSalida(horaSalida);
        asistencia.setHorasTrabajadas(new BigDecimal(horasTrabajadas));
        asistencia.setHorasExtra(new BigDecimal(horasExtra));
        asistencia.setCostoHorasExtra(new BigDecimal(costoHorasExtra));
        asistencia.setPresente(presente);
        asistencia.setObservacion(observacion);
        asistenciaRepository.save(asistencia);
    }
}
