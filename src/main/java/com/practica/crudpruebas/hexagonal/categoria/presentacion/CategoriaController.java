package com.practica.crudpruebas.hexagonal.categoria.presentacion;

import com.practica.crudpruebas.hexagonal.categoria.domain.Categoria;
import com.practica.crudpruebas.hexagonal.categoria.domain.CategoriaRepositorio;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Fijate: el Controller depende de CategoriaRepositorio (la interfaz de
// dominio), NO de CategoriaJpaRepository ni de CategoriaEntity. Si mañana
// cambiamos JPA por MongoDB, este archivo no se entera ni se toca.
// Nombre de bean explicito: existe otra clase "CategoriaController" en
// com.practica.crudpruebas.categoria.controller (el feature original) -- sin
// esto, Spring choca por 2 beans con el mismo nombre simple de clase.
@RestController("categoriaControllerHexagonal")
@RequestMapping("/api/hexagonal/categorias")
public class CategoriaController {

    private final CategoriaRepositorio categoriaRepositorio;

    public CategoriaController(CategoriaRepositorio categoriaRepositorio) {
        this.categoriaRepositorio = categoriaRepositorio;
    }

    @GetMapping
    public List<Categoria> listar() {
        return categoriaRepositorio.buscarTodas();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Categoria crear(@RequestBody Categoria categoria) {
        categoria.setId(null);
        return categoriaRepositorio.guardar(categoria);
    }
}
