package com.example.asistencia_service.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

import com.example.asistencia_service.model.Asistencia;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AsistenciaDTO {

	private Long id;

	@NotNull(message = "El id del empleado es obligatorio")
	private Long idEmpleado;

	private String nombreEmpleado;

	@NotNull(message = "La fecha es obligatoria")
	@PastOrPresent(message = "La fecha no puede ser futura")
	private LocalDate fecha;

	private LocalTime horaEntrada;
	private LocalTime horaSalida;
	private BigDecimal horasTrabajadas;
	private BigDecimal horasExtra;
	private BigDecimal costoHorasExtra;

	@NotNull(message = "El campo presente es obligatorio")
	private Boolean presente;

	private String observacion;
	private Date createdAt;
	private Date updatedAt;

	public static AsistenciaDTO fromModel(Asistencia asistencia) {
		return AsistenciaDTO.builder()
				.id(asistencia.getId())
				.idEmpleado(asistencia.getIdEmpleado())
				.nombreEmpleado(asistencia.getNombreEmpleado())
				.fecha(asistencia.getFecha())
				.horaEntrada(asistencia.getHoraEntrada())
				.horaSalida(asistencia.getHoraSalida())
				.horasTrabajadas(asistencia.getHorasTrabajadas())
				.horasExtra(asistencia.getHorasExtra())
				.costoHorasExtra(asistencia.getCostoHorasExtra())
				.presente(asistencia.getPresente())
				.observacion(asistencia.getObservacion())
				.createdAt(asistencia.getCreatedAt())
				.updatedAt(asistencia.getUpdatedAt())
				.build();
	}

	public Asistencia toModel() {
		Asistencia asistencia = new Asistencia();
		asistencia.setId(this.id);
		asistencia.setIdEmpleado(this.idEmpleado);
		asistencia.setNombreEmpleado(this.nombreEmpleado);
		asistencia.setFecha(this.fecha);
		asistencia.setHoraEntrada(this.horaEntrada);
		asistencia.setHoraSalida(this.horaSalida);
		asistencia.setHorasTrabajadas(this.horasTrabajadas);
		asistencia.setHorasExtra(this.horasExtra);
		asistencia.setCostoHorasExtra(this.costoHorasExtra);
		asistencia.setPresente(this.presente);
		asistencia.setObservacion(this.observacion);
		asistencia.setCreatedAt(this.createdAt);
		asistencia.setUpdatedAt(this.updatedAt);
		return asistencia;
	}

}
