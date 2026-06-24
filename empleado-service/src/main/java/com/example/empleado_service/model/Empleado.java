package com.example.empleado_service.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;

@Entity
@Table(name = "empleado")
public class Empleado {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(message = "El RUT del empleado es obligatorio")
	@Pattern(regexp = "^\\d{7,8}-[0-9kK]$", message = "El RUT debe tener el formato 12345678-9")
	@Column(name = "rut_empleado", nullable = false, unique = true)
	private String rutEmpleado;

	@NotBlank(message = "El nombre es obligatorio")
	@Pattern(regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñ ]+$", message = "El nombre solo puede contener letras y espacios")
	@Column(name = "nombre", nullable = false)
	private String nombre;

	@Column(name = "correo")
	private String correo;

	@Column(name = "telefono")
	private String telefono;

	@Column(name = "direccion")
	private String direccion;

	@NotBlank(message = "El cargo es obligatorio")
	@Column(name = "cargo", nullable = false)
	private String cargo;

	@NotNull(message = "La fecha de ingreso es obligatoria")
	@Past(message = "La fecha de ingreso debe ser una fecha pasada")
	@Column(name = "fecha_ingreso", nullable = false)
	private LocalDate fechaIngreso;

	@NotNull(message = "El sueldo base es obligatorio")
	@DecimalMin(value = "0.0", inclusive = false, message = "El sueldo base debe ser mayor a 0")
	@Column(name = "sueldo_base", nullable = false, precision = 10, scale = 2)
	private BigDecimal sueldoBase;

	@NotNull(message = "El valor de la hora extra es obligatorio")
	@DecimalMin(value = "0.0", inclusive = false, message = "El valor de la hora extra debe ser mayor a 0")
	@Column(name = "valor_hora_extra", nullable = false, precision = 10, scale = 2)
	private BigDecimal valorHoraExtra;

	@Column(name = "activo", nullable = false)
	private Boolean activo;

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
		if (activo == null) {
			activo = true;
		}
	}

	@PreUpdate
	protected void onUpdate() {
		updatedAt = new Date();
	}

	public Long calcularAntigüedad() {
		return ChronoUnit.YEARS.between(fechaIngreso, LocalDate.now());
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getRutEmpleado() {
		return rutEmpleado;
	}

	public void setRutEmpleado(String rutEmpleado) {
		this.rutEmpleado = rutEmpleado;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getCorreo() {
		return correo;
	}

	public void setCorreo(String correo) {
		this.correo = correo;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public String getCargo() {
		return cargo;
	}

	public void setCargo(String cargo) {
		this.cargo = cargo;
	}

	public LocalDate getFechaIngreso() {
		return fechaIngreso;
	}

	public void setFechaIngreso(LocalDate fechaIngreso) {
		this.fechaIngreso = fechaIngreso;
	}

	public BigDecimal getSueldoBase() {
		return sueldoBase;
	}

	public void setSueldoBase(BigDecimal sueldoBase) {
		this.sueldoBase = sueldoBase;
	}

	public BigDecimal getValorHoraExtra() {
		return valorHoraExtra;
	}

	public void setValorHoraExtra(BigDecimal valorHoraExtra) {
		this.valorHoraExtra = valorHoraExtra;
	}

	public Boolean getActivo() {
		return activo;
	}

	public void setActivo(Boolean activo) {
		this.activo = activo;
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
