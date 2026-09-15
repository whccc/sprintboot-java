package com.practica.crudpruebas.usuario.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    // SIEMPRE el hash (BCrypt), nunca la clave en texto plano -- ver
    // SecurityConfig.passwordEncoder() y AuthController.registrar().
    private String password;

    // Simple a proposito: un String ("USER"/"ADMIN"), no una tabla de roles
    // aparte. Alcanza para demostrar autorizacion basada en roles sin
    // agregar una relacion N:N mas al proyecto.
    private String rol;

    public Usuario() {
    }

    public Usuario(String username, String password, String rol) {
        this.username = username;
        this.password = password;
        this.rol = rol;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
}
