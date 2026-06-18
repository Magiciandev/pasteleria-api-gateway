package com.example.empleado_service.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

import com.example.empleado_service.model.Empleado;

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
	private String rutEmpleado;
	private String nombre;
	private String correo;
	private String telefono;
	private String direccion;
	private String cargo;
	private LocalDate fechaIngreso;
	private BigDecimal sueldoBase;
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
