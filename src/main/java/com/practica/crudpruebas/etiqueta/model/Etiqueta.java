package com.practica.crudpruebas.etiqueta.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.practica.crudpruebas.producto.model.Producto;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;

import java.util.HashSet;
import java.util.Set;

@Entity
public class Etiqueta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    // Lado inverso: la tabla intermedia (producto_etiqueta) la declara Producto,
    // que es el dueno de la relacion.
    @ManyToMany(mappedBy = "etiquetas")
    @JsonIgnore
    private Set<Producto> productos = new HashSet<>();

    public Etiqueta() {
    }

    public Etiqueta(Long id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Set<Producto> getProductos() {
        return productos;
    }
}
