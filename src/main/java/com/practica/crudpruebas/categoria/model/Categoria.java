package com.practica.crudpruebas.categoria.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.practica.crudpruebas.producto.model.Producto;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    // Lado "inverso" de la relacion: no genera columna propia, solo permite
    // navegar de Categoria hacia sus Productos. El dueno de la relacion
    // (quien tiene la columna categoria_id) es Producto.
    @OneToMany(mappedBy = "categoria")
    @JsonIgnore
    private List<Producto> productos = new ArrayList<>();

    public Categoria() {
    }

    public Categoria(Long id, String nombre) {
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

    public List<Producto> getProductos() {
        return productos;
    }
}
