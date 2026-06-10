package com.pasteleria.inventario.controller;

import com.pasteleria.inventario.dto.InventarioItemDTO;
import com.pasteleria.inventario.service.InventarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/inventario")
@Tag(name = "Inventario", description = "Gestión del inventario y stock de la pastelería")
public class InventarioController {
    
    @Autowired
    private InventarioService inventarioService;

    @PostMapping
    @Operation(summary = "Crear item de inventario", description = "Crea un nuevo registro de inventario relacionando ingrediente y proveedor")
    @ApiResponse(responseCode = "201", description = "Item de inventario creado exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos inválidos o ingrediente/proveedor no existe")
    public ResponseEntity<InventarioItemDTO> crear(
            @Parameter(description = "Datos del item de inventario") @RequestBody InventarioItemDTO inventarioDTO) {
        try {
            InventarioItemDTO creado = inventarioService.guardar(inventarioDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(creado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    @Operation(summary = "Listar todos los items de inventario", description = "Obtiene la lista completa de inventario")
    @ApiResponse(responseCode = "200", description = "Lista de items obtenida")
    public ResponseEntity<List<InventarioItemDTO>> listar() {
        List<InventarioItemDTO> items = inventarioService.listar();
        return ResponseEntity.ok(items);
    }

    @GetMapping("/activos")
    @Operation(summary = "Listar items de inventario activos", description = "Obtiene solo los items activos")
    @ApiResponse(responseCode = "200", description = "Lista de items activos")
    public ResponseEntity<List<InventarioItemDTO>> listarActivos() {
        List<InventarioItemDTO> items = inventarioService.listarActivos();
        return ResponseEntity.ok(items);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener item de inventario por ID", description = "Busca un item específico por su identificador")
    @ApiResponse(responseCode = "200", description = "Item encontrado")
    @ApiResponse(responseCode = "404", description = "Item no encontrado")
    public ResponseEntity<InventarioItemDTO> buscarPorId(
            @Parameter(description = "ID del item") @PathVariable Long id) {
        Optional<InventarioItemDTO> item = inventarioService.buscarPorId(id);
        return item.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/ingrediente/{ingredienteId}")
    @Operation(summary = "Buscar por ingrediente", description = "Obtiene todos los proveedores de un ingrediente")
    @ApiResponse(responseCode = "200", description = "Lista de items")
    public ResponseEntity<List<InventarioItemDTO>> buscarPorIngrediente(
            @Parameter(description = "ID del ingrediente") @PathVariable Long ingredienteId) {
        List<InventarioItemDTO> items = inventarioService.buscarPorIngrediente(ingredienteId);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/proveedor/{proveedorId}")
    @Operation(summary = "Buscar por proveedor", description = "Obtiene todos los ingredientes de un proveedor")
    @ApiResponse(responseCode = "200", description = "Lista de items")
    public ResponseEntity<List<InventarioItemDTO>> buscarPorProveedor(
            @Parameter(description = "ID del proveedor") @PathVariable Long proveedorId) {
        List<InventarioItemDTO> items = inventarioService.buscarPorProveedor(proveedorId);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/ingrediente/{ingredienteId}/proveedor/{proveedorId}")
    @Operation(summary = "Buscar relación específica", description = "Busca si existe una relación entre ingrediente y proveedor")
    @ApiResponse(responseCode = "200", description = "Relación encontrada")
    @ApiResponse(responseCode = "404", description = "Relación no encontrada")
    public ResponseEntity<InventarioItemDTO> buscarPorIngredienteYProveedor(
            @Parameter(description = "ID del ingrediente") @PathVariable Long ingredienteId,
            @Parameter(description = "ID del proveedor") @PathVariable Long proveedorId) {
        Optional<InventarioItemDTO> item = inventarioService.buscarPorIngredienteYProveedor(ingredienteId, proveedorId);
        return item.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/stock-bajo")
    @Operation(summary = "Obtener items con stock bajo", description = "Obtiene los items cuyo stock actual está por debajo del mínimo")
    @ApiResponse(responseCode = "200", description = "Lista de items con stock bajo")
    public ResponseEntity<List<InventarioItemDTO>> obtenerStockBajo() {
        List<InventarioItemDTO> items = inventarioService.obtenerStockBajo();
        return ResponseEntity.ok(items);
    }

    @PatchMapping("/{id}/stock")
    @Operation(summary = "Actualizar stock", description = "Actualiza la cantidad de stock actual de un item")
    @ApiResponse(responseCode = "200", description = "Stock actualizado")
    @ApiResponse(responseCode = "404", description = "Item no encontrado")
    public ResponseEntity<InventarioItemDTO> actualizarStock(
            @Parameter(description = "ID del item") @PathVariable Long id,
            @Parameter(description = "Nueva cantidad de stock") @RequestParam Double cantidad) {
        InventarioItemDTO actualizado = inventarioService.actualizarStock(id, cantidad);
        if (actualizado != null) {
            return ResponseEntity.ok(actualizado);
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar item de inventario", description = "Actualiza todos los datos de un item de inventario")
    @ApiResponse(responseCode = "200", description = "Item actualizado")
    @ApiResponse(responseCode = "404", description = "Item no encontrado")
    public ResponseEntity<InventarioItemDTO> actualizar(
            @Parameter(description = "ID del item") @PathVariable Long id,
            @Parameter(description = "Datos actualizados") @RequestBody InventarioItemDTO inventarioDTO) {
        InventarioItemDTO actualizado = inventarioService.actualizar(id, inventarioDTO);
        if (actualizado != null) {
            return ResponseEntity.ok(actualizado);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar item de inventario", description = "Elimina un item del inventario")
    @ApiResponse(responseCode = "204", description = "Item eliminado")
    @ApiResponse(responseCode = "404", description = "Item no encontrado")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID del item") @PathVariable Long id) {
        if (inventarioService.eliminar(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}/exists")
    @Operation(summary = "Verificar existencia de item", description = "Verifica si un item existe por su ID")
    @ApiResponse(responseCode = "200", description = "Resultado de verificación")
    public ResponseEntity<Boolean> existe(
            @Parameter(description = "ID del item") @PathVariable Long id) {
        return ResponseEntity.ok(inventarioService.existePorId(id));
    }
}
