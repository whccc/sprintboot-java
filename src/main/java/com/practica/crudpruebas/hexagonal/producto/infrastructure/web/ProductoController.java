package com.practica.crudpruebas.hexagonal.producto.infrastructure.web;

import com.practica.crudpruebas.hexagonal.producto.application.ProductoRequest;
import com.practica.crudpruebas.hexagonal.producto.application.ProductoResponse;
import com.practica.crudpruebas.hexagonal.producto.application.ProductoService;
import com.practica.crudpruebas.hexagonal.producto.domain.DetalleProducto;
import com.practica.crudpruebas.hexagonal.producto.domain.ResumenProducto;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/hexagonal/productos")
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

    // --- N:1 ---

    @PutMapping("/{id}/categoria/{categoriaId}")
    public ProductoResponse asignarCategoria(@PathVariable Long id, @PathVariable Long categoriaId) {
        return productoService.asignarCategoria(id, categoriaId);
    }

    @GetMapping("/por-categoria")
    public List<ProductoResponse> porCategoria(@RequestParam String categoria) {
        return productoService.buscarPorCategoriaNombre(categoria);
    }

    // --- N:N ---

    @PutMapping("/{id}/etiquetas/{etiquetaId}")
    public ProductoResponse agregarEtiqueta(@PathVariable Long id, @PathVariable Long etiquetaId) {
        return productoService.agregarEtiqueta(id, etiquetaId);
    }

    // --- Atomicidad ---

    @PutMapping("/transferir-stock")
    public String transferirStock(@RequestParam Long origenId, @RequestParam Long destinoId, @RequestParam int cantidad) {
        productoService.transferirStock(origenId, destinoId, cantidad);
        return "transferencia OK";
    }

    // --- 1:1 ---

    @PostMapping("/{id}/detalle")
    @ResponseStatus(HttpStatus.CREATED)
    public DetalleProducto crearDetalle(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        String descripcionLarga = (String) body.get("descripcionLarga");
        int garantiaMeses = ((Number) body.get("garantiaMeses")).intValue();
        return productoService.crearDetalle(id, descripcionLarga, garantiaMeses);
    }

    // --- Paginacion ---

    @GetMapping("/pagina")
    public Page<ProductoResponse> porPagina(
            @RequestParam double precioMaximo,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "5") int tamano,
            @RequestParam(defaultValue = "precio") String ordenarPor) {
        Pageable pageable = PageRequest.of(pagina, tamano, Sort.by(ordenarPor).ascending());
        return productoService.buscarPorPrecioMaximo(precioMaximo, pageable);
    }

    // --- Projection ---

    @GetMapping("/resumen")
    public List<ResumenProducto> resumen() {
        return productoService.resumenDeProductos();
    }
}
