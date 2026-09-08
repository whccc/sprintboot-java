package com.practica.crudpruebas.producto.dto;

// Interfaz de "projection": Spring Data JPA genera una implementacion sola,
// igual que con los repositorios. Sirve para traer SOLO columnas puntuales
// (equivalente a "select new { p.Nombre, c.Nombre }" en LINQ) sin cargar
// la entidad completa ni sus relaciones.
public interface ProductoResumen {
    String getNombre();

    double getPrecio();

    String getCategoriaNombre();
}
