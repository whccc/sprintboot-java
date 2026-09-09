package com.practica.crudpruebas.hexagonal.categoria.domain;

// Objeto de DOMINIO puro. Cero anotaciones de framework (nada de @Entity,
// nada de JPA). Es Java a secas -- por eso se puede testear, instanciar y
// usar sin levantar Spring ni una base de datos.
public class Categoria {

    private Long id;
    private String nombre;

    public Categoria(Long id, String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre de la categoria es obligatorio");
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
