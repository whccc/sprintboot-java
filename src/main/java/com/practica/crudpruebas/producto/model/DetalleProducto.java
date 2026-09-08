package com.practica.crudpruebas.producto.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

@Entity
public class DetalleProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String descripcionLarga;

    private int garantiaMeses;

    // DetalleProducto es el DUENO de la relacion 1-a-1: tiene la columna
    // producto_id (unica) apuntando al producto que extiende.
    @OneToOne
    @JoinColumn(name = "producto_id", unique = true)
    @JsonIgnore
    private Producto producto;

    public DetalleProducto() {
    }

    public DetalleProducto(String descripcionLarga, int garantiaMeses) {
        this.descripcionLarga = descripcionLarga;
        this.garantiaMeses = garantiaMeses;
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

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }
}
