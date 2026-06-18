--liquibase formatted sql

--changeset liquibase:1 author:admin
CREATE TABLE asistencia (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_empleado BIGINT NOT NULL,
    nombre_empleado VARCHAR(255) NOT NULL,
    fecha DATE NOT NULL,
    hora_entrada TIME NOT NULL,
    hora_salida TIME NOT NULL,
    horas_trabajadas DECIMAL(4,2) NOT NULL,
    horas_extra DECIMAL(4,2) NOT NULL DEFAULT 0,
    costo_horas_extra DECIMAL(10,2) NOT NULL DEFAULT 0,
    presente BOOLEAN NOT NULL DEFAULT TRUE,
    observacion VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

--changeset liquibase:2 author:admin
INSERT INTO asistencia (id_empleado, nombre_empleado, fecha, hora_entrada, hora_salida, horas_trabajadas, horas_extra, costo_horas_extra, presente, observacion) VALUES
(1, 'Juan Pérez González', '2025-06-01', '08:00:00', '17:30:00', 9.50, 0.50, 7500.00, TRUE, 'Trabajó normalmente'),
(2, 'María García López', '2025-06-01', '08:15:00', '17:45:00', 9.50, 0.50, 7750.00, TRUE, 'Entrada retrasada'),
(3, 'Carlos Rodríguez Martín', '2025-06-02', '08:00:00', '18:00:00', 10.00, 1.00, 12000.00, TRUE, 'Trabajó hora extra'),
(4, 'Rosa Martínez Flores', '2025-06-02', '08:00:00', '17:00:00', 9.00, 0.00, 0.00, TRUE, 'Jornada normal'),
(5, 'Roberto López Silva', '2025-06-03', '07:30:00', '17:30:00', 10.00, 1.00, 17000.00, TRUE, 'Entrada temprana'),
(1, 'Juan Pérez González', '2025-06-04', '08:00:00', '16:30:00', 8.50, 0.00, 0.00, FALSE, 'No asistió'),
(2, 'María García López', '2025-06-04', '08:00:00', '18:30:00', 10.50, 1.50, 11625.00, TRUE, 'Trabajó 10.5 horas'),
(3, 'Carlos Rodríguez Martín', '2025-06-05', '08:00:00', '17:00:00', 9.00, 0.00, 0.00, TRUE, 'Jornada normal'),
(4, 'Rosa Martínez Flores', '2025-06-05', '08:00:00', '18:15:00', 10.25, 1.25, 13750.00, TRUE, 'Trabajó horas extra'),
(5, 'Roberto López Silva', '2025-06-06', '08:00:00', '17:30:00', 9.50, 0.50, 8500.00, TRUE, 'Semana completa');
