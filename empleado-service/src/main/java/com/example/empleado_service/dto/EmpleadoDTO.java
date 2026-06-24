package com.example.empleado_service.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

import com.example.empleado_service.model.Empleado;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmpleadoDTO {

	private Long id;

	@NotBlank(message = "El RUT del empleado es obligatorio")
	@Pattern(regexp = "^\\d{7,8}-[0-9kK]$", message = "El RUT debe tener el formato 12345678-9")
	private String rutEmpleado;

	@NotBlank(message = "El nombre es obligatorio")
	@Pattern(regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñ ]+$", message = "El nombre solo puede contener letras y espacios")
	private String nombre;

	private String correo;
	private String telefono;
	private String direccion;

	@NotBlank(message = "El cargo es obligatorio")
	private String cargo;

	@NotNull(message = "La fecha de ingreso es obligatoria")
	@Past(message = "La fecha de ingreso debe ser una fecha pasada")
	private LocalDate fechaIngreso;

	@NotNull(message = "El sueldo base es obligatorio")
	@DecimalMin(value = "0.0", inclusive = false, message = "El sueldo base debe ser mayor a 0")
	private BigDecimal sueldoBase;

	@NotNull(message = "El valor de la hora extra es obligatorio")
	@DecimalMin(value = "0.0", inclusive = false, message = "El valor de la hora extra debe ser mayor a 0")
	private BigDecimal valorHoraExtra;

	private Boolean activo;
	private Date createdAt;
	private Date updatedAt;
	private Long antiguedad;

	public static EmpleadoDTO fromModel(Empleado empleado) {
		return EmpleadoDTO.builder()
				.id(empleado.getId())
				.rutEmpleado(empleado.getRutEmpleado())
				.nombre(empleado.getNombre())
				.correo(empleado.getCorreo())
				.telefono(empleado.getTelefono())
				.direccion(empleado.getDireccion())
				.cargo(empleado.getCargo())
				.fechaIngreso(empleado.getFechaIngreso())
				.sueldoBase(empleado.getSueldoBase())
				.valorHoraExtra(empleado.getValorHoraExtra())
				.activo(empleado.getActivo())
				.createdAt(empleado.getCreatedAt())
				.updatedAt(empleado.getUpdatedAt())
				.antiguedad(empleado.calcularAntigüedad())
				.build();
	}

	public Empleado toModel() {
		Empleado empleado = new Empleado();
		empleado.setId(this.id);
		empleado.setRutEmpleado(this.rutEmpleado);
		empleado.setNombre(this.nombre);
		empleado.setCorreo(this.correo);
		empleado.setTelefono(this.telefono);
		empleado.setDireccion(this.direccion);
		empleado.setCargo(this.cargo);
		empleado.setFechaIngreso(this.fechaIngreso);
		empleado.setSueldoBase(this.sueldoBase);
		empleado.setValorHoraExtra(this.valorHoraExtra);
		empleado.setActivo(this.activo);
		empleado.setCreatedAt(this.createdAt);
		empleado.setUpdatedAt(this.updatedAt);
		return empleado;
	}

}
