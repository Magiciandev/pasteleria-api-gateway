package com.pasteleria.inventario.dto;

public class InventarioItemDTO {
    private Long id;
    private Long ingredienteId;
    private Long proveedorId;
    private Double stockActual;
    private Double stockMinimo;
    private Double stockMaximo;
    private Double precioCompra;
    private String unidadMedida;
    private Boolean activo;

    public InventarioItemDTO() {}

    public InventarioItemDTO(Long id, Long ingredienteId, Long proveedorId, Double stockActual,
                            Double stockMinimo, Double stockMaximo, Double precioCompra, 
                            String unidadMedida, Boolean activo) {
        this.id = id;
        this.ingredienteId = ingredienteId;
        this.proveedorId = proveedorId;
        this.stockActual = stockActual;
        this.stockMinimo = stockMinimo;
        this.stockMaximo = stockMaximo;
        this.precioCompra = precioCompra;
        this.unidadMedida = unidadMedida;
        this.activo = activo;
    }

    public InventarioItemDTO(Long ingredienteId, Long proveedorId, Double stockActual,
                            Double stockMinimo, Double stockMaximo, Double precioCompra, String unidadMedida) {
        this.ingredienteId = ingredienteId;
        this.proveedorId = proveedorId;
        this.stockActual = stockActual;
        this.stockMinimo = stockMinimo;
        this.stockMaximo = stockMaximo;
        this.precioCompra = precioCompra;
        this.unidadMedida = unidadMedida;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIngredienteId() {
        return ingredienteId;
    }

    public void setIngredienteId(Long ingredienteId) {
        this.ingredienteId = ingredienteId;
    }

    public Long getProveedorId() {
        return proveedorId;
    }

    public void setProveedorId(Long proveedorId) {
        this.proveedorId = proveedorId;
    }

    public Double getStockActual() {
        return stockActual;
    }

    public void setStockActual(Double stockActual) {
        this.stockActual = stockActual;
    }

    public Double getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(Double stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public Double getStockMaximo() {
        return stockMaximo;
    }

    public void setStockMaximo(Double stockMaximo) {
        this.stockMaximo = stockMaximo;
    }

    public Double getPrecioCompra() {
        return precioCompra;
    }

    public void setPrecioCompra(Double precioCompra) {
        this.precioCompra = precioCompra;
    }

    public String getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}
