package com.practica.crudpruebas.hexagonal.etiqueta.infrastructure.web;

import com.practica.crudpruebas.hexagonal.etiqueta.domain.Etiqueta;
import com.practica.crudpruebas.hexagonal.etiqueta.domain.EtiquetaRepositorio;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hexagonal/etiquetas")
public class EtiquetaController {

    private final EtiquetaRepositorio etiquetaRepositorio;

    public EtiquetaController(EtiquetaRepositorio etiquetaRepositorio) {
        this.etiquetaRepositorio = etiquetaRepositorio;
    }

    @GetMapping
    public List<Etiqueta> listar() {
        return etiquetaRepositorio.buscarTodas();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Etiqueta crear(@RequestBody Etiqueta etiqueta) {
        etiqueta.setId(null);
        return etiquetaRepositorio.guardar(etiqueta);
    }
}
