package com.example.asistencia_service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import com.example.asistencia_service.model.Asistencia;
import com.example.asistencia_service.repository.AsistenciaRepository;

@SpringBootTest
@TestPropertySource(properties = {
		"spring.datasource.url=jdbc:h2:mem:testdb",
		"spring.datasource.driver-class-name=org.h2.Driver",
		"spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
		"spring.liquibase.enabled=false"
})
class AsistenciaServiceApplicationTests {

	@Autowired
	private AsistenciaRepository asistenciaRepository;

	@Test
	void contextLoads() {
		assertNotNull(asistenciaRepository);
	}

	@Test
	void testGuardarAsistencia() {
		Asistencia asistencia = new Asistencia();
		asistencia.setIdEmpleado(1L);
		asistencia.setNombreEmpleado("Test Empleado");
		asistencia.setFecha(LocalDate.now());
		asistencia.setHoraEntrada(LocalTime.of(8, 0));
		asistencia.setHoraSalida(LocalTime.of(17, 30));
		asistencia.setHorasTrabajadas(new BigDecimal("9.50"));
		asistencia.setHorasExtra(new BigDecimal("0.50"));
		asistencia.setCostoHorasExtra(new BigDecimal("7500.00"));
		asistencia.setPresente(true);

		Asistencia guardada = asistenciaRepository.save(asistencia);
		assertNotNull(guardada.getId());
		assertEquals("Test Empleado", guardada.getNombreEmpleado());
		assertEquals(true, guardada.getPresente());
	}

	@Test
	void testBuscarPorEmpleado() {
		Asistencia asistencia = new Asistencia();
		asistencia.setIdEmpleado(2L);
		asistencia.setNombreEmpleado("Otro Empleado");
		asistencia.setFecha(LocalDate.now());
		asistencia.setHoraEntrada(LocalTime.of(8, 0));
		asistencia.setHoraSalida(LocalTime.of(17, 0));
		asistencia.setHorasTrabajadas(new BigDecimal("9.00"));
		asistencia.setHorasExtra(new BigDecimal("0.00"));
		asistencia.setCostoHorasExtra(new BigDecimal("0.00"));
		asistencia.setPresente(true);
		asistenciaRepository.save(asistencia);

		var encontradas = asistenciaRepository.findByIdEmpleado(2L);
		assertEquals(1, encontradas.size());
		assertEquals("Otro Empleado", encontradas.get(0).getNombreEmpleado());
	}

	@Test
	void testBuscarPorFecha() {
		LocalDate hoy = LocalDate.now();
		Asistencia asistencia = new Asistencia();
		asistencia.setIdEmpleado(3L);
		asistencia.setNombreEmpleado("Empleado Fecha");
		asistencia.setFecha(hoy);
		asistencia.setHoraEntrada(LocalTime.of(8, 0));
		asistencia.setHoraSalida(LocalTime.of(18, 0));
		asistencia.setHorasTrabajadas(new BigDecimal("10.00"));
		asistencia.setHorasExtra(new BigDecimal("1.00"));
		asistencia.setCostoHorasExtra(new BigDecimal("12000.00"));
		asistencia.setPresente(true);
		asistenciaRepository.save(asistencia);

		var encontradas = asistenciaRepository.findByFecha(hoy);
		assertEquals(1, encontradas.size());
	}

	@Test
	void testBuscarPorPresente() {
		Asistencia asistencia1 = new Asistencia();
		asistencia1.setIdEmpleado(4L);
		asistencia1.setNombreEmpleado("Presente");
		asistencia1.setFecha(LocalDate.now());
		asistencia1.setHoraEntrada(LocalTime.of(8, 0));
		asistencia1.setHoraSalida(LocalTime.of(17, 0));
		asistencia1.setHorasTrabajadas(new BigDecimal("9.00"));
		asistencia1.setHorasExtra(new BigDecimal("0.00"));
		asistencia1.setCostoHorasExtra(new BigDecimal("0.00"));
		asistencia1.setPresente(true);
		asistenciaRepository.save(asistencia1);

		var presentes = asistenciaRepository.findByPresente(true);
		assertEquals(1, presentes.size());
	}

}
