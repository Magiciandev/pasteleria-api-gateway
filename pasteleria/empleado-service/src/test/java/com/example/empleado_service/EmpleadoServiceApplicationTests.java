package com.example.empleado_service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import com.example.empleado_service.model.Empleado;
import com.example.empleado_service.repository.EmpleadoRepository;
import com.example.empleado_service.service.EmpleadoService;

@SpringBootTest
@TestPropertySource(properties = {
		"spring.datasource.url=jdbc:h2:mem:testdb",
		"spring.datasource.driver-class-name=org.h2.Driver",
		"spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
		"spring.liquibase.enabled=false"
})
class EmpleadoServiceApplicationTests {

	@Autowired
	private EmpleadoService empleadoService;

	@Autowired
	private EmpleadoRepository empleadoRepository;

	@Test
	void contextLoads() {
		assertNotNull(empleadoService);
		assertNotNull(empleadoRepository);
	}

	@Test
	void testGuardarEmpleado() {
		Empleado empleado = new Empleado();
		empleado.setRutEmpleado("11111111-1");
		empleado.setNombre("Test Empleado");
		empleado.setCargo("Pastelero");
		empleado.setFechaIngreso(LocalDate.now());
		empleado.setSueldoBase(new BigDecimal("1500000.00"));
		empleado.setValorHoraExtra(new BigDecimal("15000.00"));

		Empleado guardado = empleadoService.guardar(empleado);
		assertNotNull(guardado.getId());
		assertEquals("Test Empleado", guardado.getNombre());
		assertTrue(guardado.getActivo());
	}

	@Test
	void testBuscarPorRut() {
		Empleado empleado = new Empleado();
		empleado.setRutEmpleado("22222222-2");
		empleado.setNombre("Otro Empleado");
		empleado.setCargo("Repartidor");
		empleado.setFechaIngreso(LocalDate.now());
		empleado.setSueldoBase(new BigDecimal("1200000.00"));
		empleado.setValorHoraExtra(new BigDecimal("12000.00"));
		empleadoService.guardar(empleado);

		var encontrado = empleadoService.buscarPorRut("22222222-2");
		assertTrue(encontrado.isPresent());
		assertEquals("Otro Empleado", encontrado.get().getNombre());
	}

	@Test
	void testListarActivos() {
		Empleado empleado1 = new Empleado();
		empleado1.setRutEmpleado("33333333-3");
		empleado1.setNombre("Empleado Activo");
		empleado1.setCargo("Cajero");
		empleado1.setFechaIngreso(LocalDate.now());
		empleado1.setSueldoBase(new BigDecimal("1100000.00"));
		empleado1.setValorHoraExtra(new BigDecimal("11000.00"));
		empleadoService.guardar(empleado1);

		var activos = empleadoService.listarActivos();
		assertTrue(activos.size() > 0);
	}

	@Test
	void testCalcularAntigüedad() {
		Empleado empleado = new Empleado();
		empleado.setRutEmpleado("44444444-4");
		empleado.setNombre("Empleado Antiguo");
		empleado.setCargo("Pastelero");
		empleado.setFechaIngreso(LocalDate.of(2020, 1, 1));
		empleado.setSueldoBase(new BigDecimal("1500000.00"));
		empleado.setValorHoraExtra(new BigDecimal("15000.00"));

		Long antiguedad = empleado.calcularAntigüedad();
		assertTrue(antiguedad >= 4);
	}

}
