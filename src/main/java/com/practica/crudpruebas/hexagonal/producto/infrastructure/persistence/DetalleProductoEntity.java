package com.practica.crudpruebas.hexagonal.producto.infrastructure.persistence;

import jakarta.persistence.*;

@Entity
@Table(name = "detalle_producto_hex")
public class DetalleProductoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String descripcionLarga;
    private int garantiaMeses;

    @OneToOne
    @JoinColumn(name = "producto_id", unique = true)
    private ProductoEntity producto;

    public DetalleProductoEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescripcionLarga() {
        return descripcionLarga;
    }

    public void setDescripcionLarga(String descripcionLarga) {
        this.descripcionLarga = descripcionLarga;
    }

    public int getGarantiaMeses() {
        return garantiaMeses;
    }

    public void setGarantiaMeses(int garantiaMeses) {
        this.garantiaMeses = garantiaMeses;
    }

    public ProductoEntity getProducto() {
        return producto;
    }

    public void setProducto(ProductoEntity producto) {
        this.producto = producto;
    }
}
