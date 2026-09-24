package com.practica.crudpruebas.clean.producto.usecases.boundary;

import com.practica.crudpruebas.clean.producto.entities.Producto;

import java.util.List;
import java.util.Optional;

// El "Output Boundary" hacia la persistencia -- OJO donde vive: en
// usecases/, no en entities/. Esa es la diferencia real de vocabulario
// frente a Hexagonal (donde el puerto ProductoRepositorio vive en
// domain/): en Clean Architecture, quien declara "que necesito de
// afuera" es el caso de uso -- la Entity ni se entera de que existe
// persistencia. Sigue siendo Inversion de Dependencias igual: el
// Gateway (en adapters/, mas afuera) implementa una interfaz definida
// por una capa mas interna (usecases/).
public interface ProductoRepository {
    Producto guardar(Producto producto);

    Optional<Producto> buscarPorId(Long id);

    List<Producto> listarTodos();

    void eliminar(Long id);
}
