package com.example.asistencia_service.dto;

import java.math.BigDecimal;

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
	private String nombre;
	private BigDecimal sueldoBase;
	private BigDecimal valorHoraExtra;

}
