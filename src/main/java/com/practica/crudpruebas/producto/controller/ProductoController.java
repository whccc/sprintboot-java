package com.practica.crudpruebas.producto.controller;

import com.practica.crudpruebas.producto.dto.ProductoRequest;
import com.practica.crudpruebas.producto.dto.ProductoResponse;
import com.practica.crudpruebas.producto.dto.ProductoResumen;
import com.practica.crudpruebas.producto.model.DetalleProducto;
import com.practica.crudpruebas.producto.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    public List<ProductoResponse> listar() {
        return productoService.listarTodos();
    }

    @GetMapping("/buscar")
    public List<ProductoResponse> buscarPorNombre(@RequestParam String nombre) {
        return productoService.buscarPorNombre(nombre);
    }

    @GetMapping("/{id}")
    public ProductoResponse obtener(@PathVariable Long id) {
        return productoService.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductoResponse crear(@Valid @RequestBody ProductoRequest request) {
        return productoService.crear(request);
    }

    @PutMapping("/{id}")
    public ProductoResponse actualizar(@PathVariable Long id, @Valid @RequestBody ProductoRequest request) {
        return productoService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        productoService.eliminar(id);
    }

    @PutMapping("/{id}/demo-sin-readonly")
    public String demoSinReadOnly(@PathVariable Long id, @RequestParam String nuevoNombre) {
        productoService.demoSinReadOnly(id, nuevoNombre);
        return "ejecutado sin @Transactional(readOnly) -- revisa el GET";
    }

    @PutMapping("/{id}/demo-con-readonly")
    public String demoConReadOnly(@PathVariable Long id, @RequestParam String nuevoNombre) {
        productoService.demoConReadOnly(id, nuevoNombre);
        return "ejecutado CON @Transactional(readOnly=true) -- revisa el GET";
    }

    @PutMapping("/{id}/demo-flujo-mixto")
    public String demoFlujoMixto(@PathVariable Long id, @RequestParam String nuevoNombre) {
        productoService.demoFlujoMixto(id, nuevoNombre);
        return "ejecutado con save() explicito dentro de un metodo readOnly=true -- revisa el GET";
    }

    @PutMapping("/transferir-stock")
    public String transferirStock(@RequestParam Long origenId, @RequestParam Long destinoId, @RequestParam int cantidad) {
        productoService.transferirStock(origenId, destinoId, cantidad);
        return "transferencia OK";
    }

    @PutMapping("/transferir-stock-sin-excepcion")
    public String transferirStockSinExcepcion(@RequestParam Long origenId, @RequestParam Long destinoId, @RequestParam int cantidad) {
        boolean exito = productoService.transferirStockSinExcepcion(origenId, destinoId, cantidad);
        return "resultado: " + exito;
    }

    // --- N:1 ---

    @PutMapping("/{id}/categoria/{categoriaId}")
    public ProductoResponse asignarCategoria(@PathVariable Long id, @PathVariable Long categoriaId) {
        return productoService.asignarCategoria(id, categoriaId);
    }

    // Demo: misma busqueda, dos caminos, para comparar el SQL generado en consola.
    @GetMapping("/por-categoria/n-mas-uno")
    public List<ProductoResponse> porCategoriaConNMasUno(@RequestParam String categoria) {
        return productoService.buscarPorCategoriaSinFetch(categoria);
    }

    @GetMapping("/por-categoria/optimizado")
    public List<ProductoResponse> porCategoriaOptimizado(@RequestParam String categoria) {
        return productoService.buscarPorCategoriaConFetch(categoria);
    }

    // --- N:N ---

    @PutMapping("/{id}/etiquetas/{etiquetaId}")
    public ProductoResponse agregarEtiqueta(@PathVariable Long id, @PathVariable Long etiquetaId) {
        return productoService.agregarEtiqueta(id, etiquetaId);
    }

    // --- 1:1 ---

    @PostMapping("/{id}/detalle")
    @ResponseStatus(HttpStatus.CREATED)
    public DetalleProducto crearDetalle(@PathVariable Long id, @RequestBody DetalleProducto detalle) {
        return productoService.crearDetalle(id, detalle);
    }

    // --- Paginacion y orden ---

    @GetMapping("/pagina")
    public Page<ProductoResponse> porPagina(
            @RequestParam double precioMaximo,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "5") int tamano,
            @RequestParam(defaultValue = "precio") String ordenarPor) {
        Pageable pageable = PageRequest.of(pagina, tamano, Sort.by(ordenarPor).ascending());
        return productoService.buscarPorPrecioMaximo(precioMaximo, pageable);
    }

    // --- Joins explicitos ---

    @GetMapping("/por-categoria-y-etiqueta")
    public List<ProductoResponse> porCategoriaYEtiqueta(@RequestParam String categoria, @RequestParam String etiqueta) {
        return productoService.buscarPorCategoriaYEtiqueta(categoria, etiqueta);
    }

    @GetMapping("/por-categoria-y-etiqueta/nativo")
    public List<ProductoResponse> porCategoriaYEtiquetaNativo(@RequestParam String categoria, @RequestParam String etiqueta) {
        return productoService.buscarPorCategoriaYEtiquetaNativo(categoria, etiqueta);
    }

    @GetMapping("/resumen")
    public List<ProductoResumen> resumen() {
        return productoService.resumenDeProductos();
    }
}
