package com.example.asistencia_service.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.asistencia_service.model.Asistencia;

@Repository
public interface AsistenciaRepository extends JpaRepository<Asistencia, Long> {

	List<Asistencia> findByIdEmpleado(Long idEmpleado);

	List<Asistencia> findByFecha(LocalDate fecha);

	Optional<Asistencia> findByIdEmpleadoAndFecha(Long idEmpleado, LocalDate fecha);

	List<Asistencia> findByPresente(Boolean presente);

}
