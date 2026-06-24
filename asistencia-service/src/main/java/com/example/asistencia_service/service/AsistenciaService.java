package com.example.asistencia_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.example.asistencia_service.exception.BadRequestException;
import com.example.asistencia_service.exception.ExternalServiceException;
import com.example.asistencia_service.exception.ResourceNotFoundException;
import com.example.asistencia_service.dto.EmpleadoDTO;
import com.example.asistencia_service.model.Asistencia;
import com.example.asistencia_service.repository.AsistenciaRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class AsistenciaService {

    private static final Logger logger = LoggerFactory.getLogger(AsistenciaService.class);

    private final AsistenciaRepository asistenciaRepository;
    private final WebClient.Builder webClientBuilder;
    private static final String EMPLEADO_SERVICE_URL = "http://empleado-service:9092";

    public AsistenciaService(AsistenciaRepository asistenciaRepository, WebClient.Builder webClientBuilder) {
        this.asistenciaRepository = asistenciaRepository;
        this.webClientBuilder = webClientBuilder;
    }

    public Asistencia registrar(Asistencia asistencia) {
        logger.info("Registrando asistencia: idEmpleado={}, fecha={}", asistencia.getIdEmpleado(), asistencia.getFecha());
        EmpleadoDTO empleado = obtenerEmpleado(asistencia.getIdEmpleado());
        asistencia.setNombreEmpleado(empleado.getNombre());

        if (!asistencia.getPresente()) {
            asistencia.setHorasTrabajadas(BigDecimal.ZERO);
            asistencia.setHorasExtra(BigDecimal.ZERO);
            asistencia.setCostoHorasExtra(BigDecimal.ZERO);
        } else {
            if (asistencia.getHoraEntrada() == null || asistencia.getHoraSalida() == null) {
                throw new BadRequestException("Hora de entrada y salida son requeridas cuando el empleado está presente");
            }
            long minutos = ChronoUnit.MINUTES.between(asistencia.getHoraEntrada(), asistencia.getHoraSalida());
            if (minutos <= 0) throw new BadRequestException("La hora de salida debe ser posterior a la hora de entrada");

            BigDecimal horas = new BigDecimal(minutos).divide(new BigDecimal(60), 2, BigDecimal.ROUND_HALF_UP);
            asistencia.setHorasTrabajadas(horas);

            BigDecimal horasExtra = BigDecimal.ZERO;
            if (horas.compareTo(new BigDecimal("9")) > 0) {
                horasExtra = horas.subtract(new BigDecimal("9"));
            }
            asistencia.setHorasExtra(horasExtra);
            asistencia.setCostoHorasExtra(horasExtra.multiply(empleado.getValorHoraExtra()));
        }

        Asistencia guardada = asistenciaRepository.save(asistencia);
        logger.info("Asistencia registrada exitosamente id={}", guardada.getId());
        return guardada;
    }

    private EmpleadoDTO obtenerEmpleado(Long idEmpleado) {
        try {
            EmpleadoDTO empleado = webClientBuilder.build()
                    .get()
                    .uri(EMPLEADO_SERVICE_URL + "/empleados/{id}", idEmpleado)
                    .retrieve()
                    .bodyToMono(EmpleadoDTO.class)
                    .block();
            if (empleado == null) throw new ResourceNotFoundException("Empleado con id " + idEmpleado + " no encontrado");
            return empleado;
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error al conectar con empleado-service: {}", e.getMessage(), e);
            throw new ExternalServiceException("Error al conectar con empleado-service: " + e.getMessage());
        }
    }

    public List<Asistencia> listar() {
        logger.info("Listando todas las asistencias");
        return asistenciaRepository.findAll();
    }

    public Asistencia buscarPorId(Long id) {
        logger.info("Buscando asistencia id={}", id);
        return asistenciaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Asistencia con id " + id + " no encontrada"));
    }

    public List<Asistencia> buscarPorEmpleado(Long idEmpleado) {
        return asistenciaRepository.findByIdEmpleado(idEmpleado);
    }

    public List<Asistencia> buscarPorFecha(LocalDate fecha) {
        return asistenciaRepository.findByFecha(fecha);
    }

    public Optional<Asistencia> buscarPorEmpleadoYFecha(Long idEmpleado, LocalDate fecha) {
        return asistenciaRepository.findByIdEmpleadoAndFecha(idEmpleado, fecha);
    }

    public Asistencia actualizar(Long id, Asistencia detalles) {
        logger.info("Actualizando asistencia id={}", id);
        Asistencia asistencia = asistenciaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Asistencia con id " + id + " no encontrada"));
        asistencia.setHoraEntrada(detalles.getHoraEntrada());
        asistencia.setHoraSalida(detalles.getHoraSalida());
        asistencia.setPresente(detalles.getPresente());
        asistencia.setObservacion(detalles.getObservacion());
        EmpleadoDTO empleado = obtenerEmpleado(asistencia.getIdEmpleado());
        if (!asistencia.getPresente()) {
            asistencia.setHorasTrabajadas(BigDecimal.ZERO);
            asistencia.setHorasExtra(BigDecimal.ZERO);
            asistencia.setCostoHorasExtra(BigDecimal.ZERO);
        } else {
            long minutos = ChronoUnit.MINUTES.between(asistencia.getHoraEntrada(), asistencia.getHoraSalida());
            BigDecimal horas = new BigDecimal(minutos).divide(new BigDecimal(60), 2, BigDecimal.ROUND_HALF_UP);
            asistencia.setHorasTrabajadas(horas);
            BigDecimal horasExtra = horas.compareTo(new BigDecimal("9")) > 0 ? horas.subtract(new BigDecimal("9")) : BigDecimal.ZERO;
            asistencia.setHorasExtra(horasExtra);
            asistencia.setCostoHorasExtra(horasExtra.multiply(empleado.getValorHoraExtra()));
        }
        return asistenciaRepository.save(asistencia);
    }

    public void eliminar(Long id) {
        logger.info("Eliminando asistencia id={}", id);
        if (!asistenciaRepository.existsById(id)) throw new ResourceNotFoundException("Asistencia con id " + id + " no encontrada");
        asistenciaRepository.deleteById(id);
        logger.info("Asistencia eliminada exitosamente id={}", id);
    }
}
