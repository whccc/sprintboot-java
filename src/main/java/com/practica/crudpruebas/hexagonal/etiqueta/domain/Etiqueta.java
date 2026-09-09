package com.practica.crudpruebas.hexagonal.etiqueta.domain;

public class Etiqueta {

    private Long id;
    private String nombre;

    public Etiqueta(Long id, String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre de la etiqueta es obligatorio");
        }
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
}
