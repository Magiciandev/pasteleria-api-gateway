package com.example.asistencia_service.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.asistencia_service.dto.EmpleadoDTO;
import com.example.asistencia_service.model.Asistencia;
import com.example.asistencia_service.repository.AsistenciaRepository;

@Service
public class AsistenciaService {

	@Autowired
	private AsistenciaRepository asistenciaRepository;

	@Autowired
	private WebClient.Builder webClientBuilder;

	private static final String EMPLEADO_SERVICE_URL = "http://localhost:9092";

	public Asistencia registrar(Asistencia asistencia) {
		// 1. Obtener datos del empleado desde empleado-service
		EmpleadoDTO empleado = obtenerEmpleado(asistencia.getIdEmpleado());
		if (empleado == null) {
			throw new IllegalArgumentException(
					"El empleado con ID " + asistencia.getIdEmpleado() + " no existe");
		}

		// 2. Establecer nombre del empleado
		asistencia.setNombreEmpleado(empleado.getNombre());

		// 3. Si no está presente, establecer horas en 0
		if (!asistencia.getPresente()) {
			asistencia.setHorasTrabajadas(BigDecimal.ZERO);
			asistencia.setHorasExtra(BigDecimal.ZERO);
			asistencia.setCostoHorasExtra(BigDecimal.ZERO);
		} else {
			// 4. Calcular horas trabajadas
			long minutosTranscurridos = ChronoUnit.MINUTES.between(asistencia.getHoraEntrada(),
					asistencia.getHoraSalida());
			BigDecimal horasDecimales = new BigDecimal(minutosTranscurridos).divide(new BigDecimal(60), 2,
					BigDecimal.ROUND_HALF_UP);
			asistencia.setHorasTrabajadas(horasDecimales);

			// 5. Calcular horas extra (si horas_trabajadas > 9)
			BigDecimal horasExtra = BigDecimal.ZERO;
			if (horasDecimales.compareTo(new BigDecimal("9")) > 0) {
				horasExtra = horasDecimales.subtract(new BigDecimal("9"));
			}
			asistencia.setHorasExtra(horasExtra);

			// 6. Calcular costo horas extra
			BigDecimal costoHorasExtra = horasExtra.multiply(empleado.getValorHoraExtra());
			asistencia.setCostoHorasExtra(costoHorasExtra);
		}

		return asistenciaRepository.save(asistencia);
	}

	public List<Asistencia> listar() {
		return asistenciaRepository.findAll();
	}

	public Optional<Asistencia> buscarPorId(Long id) {
		return asistenciaRepository.findById(id);
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
		Optional<Asistencia> asistenciaOpt = asistenciaRepository.findById(id);
		if (asistenciaOpt.isPresent()) {
			Asistencia asistencia = asistenciaOpt.get();

			// Actualizar campos permitidos
			asistencia.setHoraEntrada(detalles.getHoraEntrada());
			asistencia.setHoraSalida(detalles.getHoraSalida());
			asistencia.setPresente(detalles.getPresente());
			asistencia.setObservacion(detalles.getObservacion());

			// Recalcular horas
			EmpleadoDTO empleado = obtenerEmpleado(asistencia.getIdEmpleado());
			if (empleado != null) {
				if (!asistencia.getPresente()) {
					asistencia.setHorasTrabajadas(BigDecimal.ZERO);
					asistencia.setHorasExtra(BigDecimal.ZERO);
					asistencia.setCostoHorasExtra(BigDecimal.ZERO);
				} else {
					// Calcular horas trabajadas
					long minutosTranscurridos = ChronoUnit.MINUTES.between(asistencia.getHoraEntrada(),
							asistencia.getHoraSalida());
					BigDecimal horasDecimales = new BigDecimal(minutosTranscurridos).divide(new BigDecimal(60), 2,
							BigDecimal.ROUND_HALF_UP);
					asistencia.setHorasTrabajadas(horasDecimales);

					// Calcular horas extra
					BigDecimal horasExtra = BigDecimal.ZERO;
					if (horasDecimales.compareTo(new BigDecimal("9")) > 0) {
						horasExtra = horasDecimales.subtract(new BigDecimal("9"));
					}
					asistencia.setHorasExtra(horasExtra);

					// Calcular costo horas extra
					BigDecimal costoHorasExtra = horasExtra.multiply(empleado.getValorHoraExtra());
					asistencia.setCostoHorasExtra(costoHorasExtra);
				}
			}

			return asistenciaRepository.save(asistencia);
		}
		return null;
	}

	public boolean eliminar(Long id) {
		if (asistenciaRepository.existsById(id)) {
			asistenciaRepository.deleteById(id);
			return true;
		}
		return false;
	}

	private EmpleadoDTO obtenerEmpleado(Long idEmpleado) {
		try {
			EmpleadoDTO empleado = webClientBuilder.build()
					.get()
					.uri(EMPLEADO_SERVICE_URL + "/empleados/{id}", idEmpleado)
					.retrieve()
					.bodyToMono(EmpleadoDTO.class)
					.block();
			return empleado;
		} catch (Exception e) {
			return null;
		}
	}

}
