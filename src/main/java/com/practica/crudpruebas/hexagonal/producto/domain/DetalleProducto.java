package com.practica.crudpruebas.hexagonal.producto.domain;

public class DetalleProducto {

    private Long id;
    private String descripcionLarga;
    private int garantiaMeses;
    private Long productoId;

    public DetalleProducto(Long id, String descripcionLarga, int garantiaMeses, Long productoId) {
        this.id = id;
        this.descripcionLarga = descripcionLarga;
        this.garantiaMeses = garantiaMeses;
        this.productoId = productoId;
    }

    public Long getId() {
        return id;
    }

    public String getDescripcionLarga() {
        return descripcionLarga;
    }

    public int getGarantiaMeses() {
        return garantiaMeses;
    }

    public Long getProductoId() {
        return productoId;
    }
}
