package com.practica.crudpruebas.categoria.controller;

import com.practica.crudpruebas.categoria.model.Categoria;
import com.practica.crudpruebas.categoria.repository.CategoriaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    private final CategoriaRepository categoriaRepository;

    public CategoriaController(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @GetMapping
    public List<Categoria> listar() {
        return categoriaRepository.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Categoria crear(@RequestBody Categoria categoria) {
        categoria.setId(null);
        return categoriaRepository.save(categoria);
    }
}
