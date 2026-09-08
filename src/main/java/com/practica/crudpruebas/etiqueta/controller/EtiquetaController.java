package com.practica.crudpruebas.etiqueta.controller;

import com.practica.crudpruebas.etiqueta.model.Etiqueta;
import com.practica.crudpruebas.etiqueta.repository.EtiquetaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/etiquetas")
public class EtiquetaController {

    private final EtiquetaRepository etiquetaRepository;

    public EtiquetaController(EtiquetaRepository etiquetaRepository) {
        this.etiquetaRepository = etiquetaRepository;
    }

    @GetMapping
    public List<Etiqueta> listar() {
        return etiquetaRepository.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Etiqueta crear(@RequestBody Etiqueta etiqueta) {
        etiqueta.setId(null);
        return etiquetaRepository.save(etiqueta);
    }
}
