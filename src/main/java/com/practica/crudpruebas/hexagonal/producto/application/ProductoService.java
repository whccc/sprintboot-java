package com.practica.crudpruebas.hexagonal.producto.application;

import com.practica.crudpruebas.hexagonal.categoria.domain.Categoria;
import com.practica.crudpruebas.hexagonal.categoria.domain.CategoriaRepositorio;
import com.practica.crudpruebas.hexagonal.etiqueta.domain.Etiqueta;
import com.practica.crudpruebas.hexagonal.etiqueta.domain.EtiquetaRepositorio;
import com.practica.crudpruebas.hexagonal.producto.domain.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

// Capa de APLICACION: los "casos de uso". Orquesta los puertos de dominio
// (ProductoRepositorio, CategoriaRepositorio, EtiquetaRepositorio) sin saber
// NADA de JPA, Hibernate ni SQL -- eso vive 2 capas mas abajo, en
// infrastructure/persistence, detras de las interfaces.
//
// Notese que aca NO hay ningun @Transactional -- porque este Service ya NO
// llama directo a un JpaRepository, llama a los puertos (interfaces de
// dominio). La responsabilidad de abrir/cerrar transacciones se movio a los
// adaptadores (Ej: ProductoRepositorioImpl), que es quien de verdad sabe
// que hay una sesion de Hibernate de por medio.
@Service
public class ProductoService {

    private final ProductoRepositorio productoRepositorio;
    private final CategoriaRepositorio categoriaRepositorio;
    private final EtiquetaRepositorio etiquetaRepositorio;
    private final DetalleProductoRepositorio detalleProductoRepositorio;

    public ProductoService(ProductoRepositorio productoRepositorio,
                            CategoriaRepositorio categoriaRepositorio,
                            EtiquetaRepositorio etiquetaRepositorio,
                            DetalleProductoRepositorio detalleProductoRepositorio) {
        this.productoRepositorio = productoRepositorio;
        this.categoriaRepositorio = categoriaRepositorio;
        this.etiquetaRepositorio = etiquetaRepositorio;
        this.detalleProductoRepositorio = detalleProductoRepositorio;
    }

    public List<ProductoResponse> listarTodos() {
        return productoRepositorio.buscarTodos().stream().map(this::aResponse).toList();
    }

    public List<ProductoResponse> buscarPorNombre(String nombre) {
        return productoRepositorio.buscarPorNombre(nombre).stream().map(this::aResponse).toList();
    }

    public ProductoResponse buscarPorId(Long id) {
        return aResponse(buscarEntidadDeDominioPorId(id));
    }

    private Producto buscarEntidadDeDominioPorId(Long id) {
        return productoRepositorio.buscarPorId(id)
                .orElseThrow(() -> new ProductoNoEncontradoException(id));
    }

    public ProductoResponse crear(ProductoRequest request) {
        validarCategoriaSiViene(request.categoriaId());
        Producto producto = Producto.nuevo(request.nombre(), request.precio(), request.stock(), request.categoriaId());
        return aResponse(productoRepositorio.guardar(producto));
    }

    public ProductoResponse actualizar(Long id, ProductoRequest request) {
        Producto existente = buscarEntidadDeDominioPorId(id);
        validarCategoriaSiViene(request.categoriaId());
        existente.actualizarDatos(request.nombre(), request.precio(), request.stock());
        existente.asignarCategoria(request.categoriaId());
        return aResponse(productoRepositorio.guardar(existente));
    }

    public void eliminar(Long id) {
        buscarEntidadDeDominioPorId(id); // valida que exista antes de borrar
        productoRepositorio.eliminar(id);
    }

    private void validarCategoriaSiViene(Long categoriaId) {
        if (categoriaId == null) {
            return;
        }
        categoriaRepositorio.buscarPorId(categoriaId)
                .orElseThrow(() -> new IllegalArgumentException("No existe la categoria " + categoriaId));
    }

    // --- N:1 ---

    public ProductoResponse asignarCategoria(Long productoId, Long categoriaId) {
        Producto producto = buscarEntidadDeDominioPorId(productoId);
        validarCategoriaSiViene(categoriaId);
        producto.asignarCategoria(categoriaId);
        return aResponse(productoRepositorio.guardar(producto));
    }

    public List<ProductoResponse> buscarPorCategoriaNombre(String nombreCategoria) {
        return productoRepositorio.buscarPorCategoriaNombre(nombreCategoria).stream()
                .map(this::aResponse)
                .toList();
    }

    // --- N:N ---

    public ProductoResponse agregarEtiqueta(Long productoId, Long etiquetaId) {
        Producto producto = buscarEntidadDeDominioPorId(productoId);
        etiquetaRepositorio.buscarPorId(etiquetaId)
                .orElseThrow(() -> new IllegalArgumentException("No existe la etiqueta " + etiquetaId));
        producto.agregarEtiqueta(etiquetaId);
        return aResponse(productoRepositorio.guardar(producto));
    }

    // --- Atomicidad real: transferir stock entre 2 productos ---

    public void transferirStock(Long idOrigen, Long idDestino, int cantidad) {
        Producto origen = buscarEntidadDeDominioPorId(idOrigen);
        origen.descontarStock(cantidad); // valida stock suficiente, tira excepcion si no
        productoRepositorio.guardar(origen);

        Producto destino = buscarEntidadDeDominioPorId(idDestino);
        destino.aumentarStock(cantidad);
        productoRepositorio.guardar(destino);
    }

    // --- 1:1 ---

    public DetalleProducto crearDetalle(Long productoId, String descripcionLarga, int garantiaMeses) {
        buscarEntidadDeDominioPorId(productoId); // valida que el producto exista
        DetalleProducto detalle = new DetalleProducto(null, descripcionLarga, garantiaMeses, productoId);
        return detalleProductoRepositorio.guardar(detalle);
    }

    // --- Paginacion ---

    public Page<ProductoResponse> buscarPorPrecioMaximo(double precioMaximo, Pageable pageable) {
        return productoRepositorio.buscarPorPrecioMaximo(precioMaximo, pageable).map(this::aResponse);
    }

    // --- Projection ---

    public List<ResumenProducto> resumenDeProductos() {
        return productoRepositorio.resumenDeProductos();
    }

    // Aca es donde el Service "arma" el DTO de salida combinando datos de
    // 3 puertos distintos -- el precio de tener el dominio referenciando
    // solo IDs: alguien tiene que resolverlos para el cliente HTTP.
    private ProductoResponse aResponse(Producto producto) {
        String categoriaNombre = Optional.ofNullable(producto.getCategoriaId())
                .flatMap(categoriaRepositorio::buscarPorId)
                .map(Categoria::getNombre)
                .orElse(null);

        List<String> nombresEtiquetas = etiquetaRepositorio.buscarPorIds(producto.getEtiquetaIds()).stream()
                .map(Etiqueta::getNombre)
                .toList();

        return new ProductoResponse(producto.getId(), producto.getNombre(), producto.getPrecio(),
                producto.getStock(), categoriaNombre, nombresEtiquetas);
    }
}
