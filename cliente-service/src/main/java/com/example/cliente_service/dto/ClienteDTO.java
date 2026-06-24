package com.example.cliente_service.dto;

import com.example.cliente_service.model.Cliente;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClienteDTO {
    private Long id;

    @NotBlank(message = "El RUT del cliente es obligatorio")
    @Pattern(regexp = "^\\d{7,8}-[0-9kK]$", message = "El RUT debe tener el formato 12345678-9")
    private String rutCliente;

    @NotBlank(message = "El nombre es obligatorio")
    @Pattern(regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñ ]+$", message = "El nombre solo puede contener letras y espacios")
    private String nombre;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo debe tener un formato valido")
    private String correo;

    @NotBlank(message = "El telefono es obligatorio")
    @Pattern(regexp = "^\\+?\\d{8,15}$", message = "El telefono debe contener solo numeros, opcionalmente con +")
    private String telefono;

    @NotBlank(message = "La direccion es obligatoria")
    private String direccion;

    public Cliente toModel() {
        return new Cliente(id, rutCliente, nombre, correo, telefono, direccion);
    }

    public static ClienteDTO fromModel(Cliente c) {
        if (c == null) return null;
        return new ClienteDTO(c.getId(), c.getRutCliente(), c.getNombre(), c.getCorreo(), c.getTelefono(), c.getDireccion());
    }
}