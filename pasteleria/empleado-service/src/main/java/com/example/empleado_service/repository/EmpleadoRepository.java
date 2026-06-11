package com.example.empleado_service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.empleado_service.model.Empleado;

@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {

	Optional<Empleado> findByRutEmpleado(String rutEmpleado);

	List<Empleado> findByCargo(String cargo);

	List<Empleado> findByActivo(Boolean activo);

}
