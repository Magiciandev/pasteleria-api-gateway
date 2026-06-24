package com.example.asistencia_service.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

@Entity
@Table(name = "asistencia")
public class Asistencia {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull(message = "El id del empleado es obligatorio")
	@Column(name = "id_empleado", nullable = false)
	private Long idEmpleado;

	@Column(name = "nombre_empleado", nullable = false)
	private String nombreEmpleado;

	@NotNull(message = "La fecha es obligatoria")
	@PastOrPresent(message = "La fecha no puede ser futura")
	@Column(name = "fecha", nullable = false)
	private LocalDate fecha;

	@Column(name = "hora_entrada", nullable = false)
	private LocalTime horaEntrada;

	@Column(name = "hora_salida", nullable = false)
	private LocalTime horaSalida;

	@Column(name = "horas_trabajadas", nullable = false, precision = 4, scale = 2)
	private BigDecimal horasTrabajadas;

	@Column(name = "horas_extra", nullable = false, precision = 4, scale = 2)
	private BigDecimal horasExtra;

	@Column(name = "costo_horas_extra", nullable = false, precision = 10, scale = 2)
	private BigDecimal costoHorasExtra;

	@NotNull(message = "El campo presente es obligatorio")
	@Column(name = "presente", nullable = false)
	private Boolean presente;

	@Column(name = "observacion")
	private String observacion;

	@Column(name = "created_at", nullable = false, updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdAt;

	@Column(name = "updated_at", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date updatedAt;

	@PrePersist
	protected void onCreate() {
		createdAt = new Date();
		updatedAt = new Date();
	}

	@PreUpdate
	protected void onUpdate() {
		updatedAt = new Date();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getIdEmpleado() {
		return idEmpleado;
	}

	public void setIdEmpleado(Long idEmpleado) {
		this.idEmpleado = idEmpleado;
	}

	public String getNombreEmpleado() {
		return nombreEmpleado;
	}

	public void setNombreEmpleado(String nombreEmpleado) {
		this.nombreEmpleado = nombreEmpleado;
	}

	public LocalDate getFecha() {
		return fecha;
	}

	public void setFecha(LocalDate fecha) {
		this.fecha = fecha;
	}

	public LocalTime getHoraEntrada() {
		return horaEntrada;
	}

	public void setHoraEntrada(LocalTime horaEntrada) {
		this.horaEntrada = horaEntrada;
	}

	public LocalTime getHoraSalida() {
		return horaSalida;
	}

	public void setHoraSalida(LocalTime horaSalida) {
		this.horaSalida = horaSalida;
	}

	public BigDecimal getHorasTrabajadas() {
		return horasTrabajadas;
	}

	public void setHorasTrabajadas(BigDecimal horasTrabajadas) {
		this.horasTrabajadas = horasTrabajadas;
	}

	public BigDecimal getHorasExtra() {
		return horasExtra;
	}

	public void setHorasExtra(BigDecimal horasExtra) {
		this.horasExtra = horasExtra;
	}

	public BigDecimal getCostoHorasExtra() {
		return costoHorasExtra;
	}

	public void setCostoHorasExtra(BigDecimal costoHorasExtra) {
		this.costoHorasExtra = costoHorasExtra;
	}

	public Boolean getPresente() {
		return presente;
	}

	public void setPresente(Boolean presente) {
		this.presente = presente;
	}

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

}
