package com.example.proveedor_service.dto;

import com.example.proveedor_service.model.Proveedor;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProveedorDTO {

    private Long id;

    @NotBlank(message = "El nombre del proveedor es obligatorio")
    private String nombre;

    @NotBlank(message = "El RUT del proveedor es obligatorio")
    private String rut;

    @NotBlank(message = "El correo del proveedor es obligatorio")
    @Email(message = "El correo debe tener un formato válido")
    private String correo;

    private String telefono;

    private String direccion;

    @NotNull(message = "El estado activo es obligatorio")
    private Boolean activo;

    @NotNull(message = "La fecha de registro es obligatoria")
    private LocalDate fechaRegistro;

    public Proveedor toModel() {
        Proveedor proveedor = new Proveedor();
        proveedor.setId(id);
        proveedor.setNombre(nombre);
        proveedor.setRut(rut);
        proveedor.setCorreo(correo);
        proveedor.setTelefono(telefono);
        proveedor.setDireccion(direccion);
        proveedor.setActivo(activo);
        proveedor.setFechaRegistro(fechaRegistro);
        return proveedor;
    }

    public static ProveedorDTO fromModel(Proveedor p) {
        if (p == null) return null;
        return new ProveedorDTO(
            p.getId(),
            p.getNombre(),
            p.getRut(),
            p.getCorreo(),
            p.getTelefono(),
            p.getDireccion(),
            p.getActivo(),
            p.getFechaRegistro()
        );
    }
}
