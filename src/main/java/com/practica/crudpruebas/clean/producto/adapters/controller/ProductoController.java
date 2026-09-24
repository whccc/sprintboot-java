package com.practica.crudpruebas.clean.producto.adapters.controller;

import com.practica.crudpruebas.clean.producto.usecases.buscarproducto.BuscarProductoOutput;
import com.practica.crudpruebas.clean.producto.usecases.buscarproducto.BuscarProductoUseCase;
import com.practica.crudpruebas.clean.producto.usecases.crearproducto.CrearProductoInput;
import com.practica.crudpruebas.clean.producto.usecases.crearproducto.CrearProductoOutput;
import com.practica.crudpruebas.clean.producto.usecases.crearproducto.CrearProductoUseCase;
import com.practica.crudpruebas.clean.producto.usecases.eliminarproducto.EliminarProductoUseCase;
import com.practica.crudpruebas.clean.producto.usecases.listarproductos.ListarProductosOutput;
import com.practica.crudpruebas.clean.producto.usecases.listarproductos.ListarProductosUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// El Controller es un "Interface Adapter": su UNICO trabajo es traducir
// HTTP <-> Input/Output de cada Use Case. No tiene logica de negocio --
// ni siquiera conoce ProductoRepository, solo conoce los 4 Use Cases de
// mas arriba. Bean renombrado ("productoControllerClean") por la misma
// razon que en Hexagonal: 3 clases se llaman igual (ProductoController
// en producto/, hexagonal/producto/presentacion/ y aca), y Spring nombra
// los beans por default con el nombre simple de la clase -- chocarian.
@RestController("productoControllerClean")
@RequestMapping("/api/clean/productos")
public class ProductoController {

    private final CrearProductoUseCase crearProductoUseCase;
    private final BuscarProductoUseCase buscarProductoUseCase;
    private final ListarProductosUseCase listarProductosUseCase;
    private final EliminarProductoUseCase eliminarProductoUseCase;

    public ProductoController(CrearProductoUseCase crearProductoUseCase,
                               BuscarProductoUseCase buscarProductoUseCase,
                               ListarProductosUseCase listarProductosUseCase,
                               EliminarProductoUseCase eliminarProductoUseCase) {
        this.crearProductoUseCase = crearProductoUseCase;
        this.buscarProductoUseCase = buscarProductoUseCase;
        this.listarProductosUseCase = listarProductosUseCase;
        this.eliminarProductoUseCase = eliminarProductoUseCase;
    }

    @GetMapping
    public List<ListarProductosOutput> listar() {
        return listarProductosUseCase.execute();
    }

    @GetMapping("/{id}")
    public BuscarProductoOutput obtener(@PathVariable Long id) {
        return buscarProductoUseCase.execute(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CrearProductoOutput crear(@Valid @RequestBody CrearProductoInput input) {
        return crearProductoUseCase.execute(input);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        eliminarProductoUseCase.execute(id);
    }
}
