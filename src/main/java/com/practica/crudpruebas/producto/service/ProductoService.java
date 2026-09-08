package com.practica.crudpruebas.producto.service;

import com.practica.crudpruebas.categoria.model.Categoria;
import com.practica.crudpruebas.categoria.repository.CategoriaRepository;
import com.practica.crudpruebas.etiqueta.model.Etiqueta;
import com.practica.crudpruebas.etiqueta.repository.EtiquetaRepository;
import com.practica.crudpruebas.producto.dto.ProductoRequest;
import com.practica.crudpruebas.producto.dto.ProductoResponse;
import com.practica.crudpruebas.producto.dto.ProductoResumen;
import com.practica.crudpruebas.producto.exception.ProductoNoEncontradoException;
import com.practica.crudpruebas.producto.model.DetalleProducto;
import com.practica.crudpruebas.producto.model.Producto;
import com.practica.crudpruebas.producto.repository.DetalleProductoRepository;
import com.practica.crudpruebas.producto.repository.ProductoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final EtiquetaRepository etiquetaRepository;
    private final DetalleProductoRepository detalleProductoRepository;

    public ProductoService(ProductoRepository productoRepository,
                            CategoriaRepository categoriaRepository,
                            EtiquetaRepository etiquetaRepository,
                            DetalleProductoRepository detalleProductoRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
        this.etiquetaRepository = etiquetaRepository;
        this.detalleProductoRepository = detalleProductoRepository;
    }

    // @Transactional aca no es opcional: sin esto, la sesion de Hibernate solo
    // sigue viva gracias a "Open Session In View" (que ata la sesion al hilo
    // del request HTTP). Si este metodo se llama desde OTRO hilo (por ejemplo,
    // dentro de un CompletableFuture.supplyAsync), OSIV no lo cubre y
    // producto.getCategoria().getNombre() explota con LazyInitializationException.
    // Con @Transactional, el metodo abre su PROPIA sesion, sin importar que
    // hilo lo ejecute -- por eso es la forma correcta, no un parche.
    @Transactional(readOnly = true)
    public List<ProductoResponse> listarTodos() {
        return productoRepository.findAll().stream()
                .map(ProductoResponse::desde)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProductoResponse> buscarPorNombre(String nombre) {
        return productoRepository.findByNombreContainingIgnoreCase(nombre).stream()
                .map(ProductoResponse::desde)
                .toList();
    }

    // Uso interno (otros metodos del Service la necesitan como ENTIDAD,
    // no como DTO, para poder modificarla y guardarla de nuevo).
    public Producto buscarEntidadPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ProductoNoEncontradoException(id));
    }

    @Transactional(readOnly = true)
    public ProductoResponse buscarPorId(Long id) {
        return ProductoResponse.desde(buscarEntidadPorId(id));
    }

    public ProductoResponse crear(ProductoRequest request) {
        Producto producto = new Producto();
        producto.setNombre(request.nombre());
        producto.setPrecio(request.precio());
        producto.setStock(request.stock());
        asignarCategoriaSiViene(producto, request.categoriaId());
        return ProductoResponse.desde(productoRepository.save(producto));
    }

    public ProductoResponse actualizar(Long id, ProductoRequest request) {
        Producto existente = buscarEntidadPorId(id);
        existente.setNombre(request.nombre());
        existente.setPrecio(request.precio());
        existente.setStock(request.stock());
        asignarCategoriaSiViene(existente, request.categoriaId());
        return ProductoResponse.desde(productoRepository.save(existente));
    }

    public void eliminar(Long id) {
        Producto existente = buscarEntidadPorId(id);
        productoRepository.delete(existente);
    }

    private void asignarCategoriaSiViene(Producto producto, Long categoriaId) {
        if (categoriaId == null) {
            return;
        }
        Categoria categoria = categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new IllegalArgumentException("No existe la categoria " + categoriaId));
        producto.setCategoria(categoria);
    }

    // --- Relacion N:1 (Producto -> Categoria) ---

    public ProductoResponse asignarCategoria(Long productoId, Long categoriaId) {
        Producto producto = buscarEntidadPorId(productoId);
        asignarCategoriaSiViene(producto, categoriaId);
        return ProductoResponse.desde(productoRepository.save(producto));
    }

    // Version CON el problema N+1: por cada producto que devuelve la query,
    // acceder a categoria.getNombre() dispara una consulta aparte.
    @Transactional(readOnly = true)
    public List<ProductoResponse> buscarPorCategoriaSinFetch(String nombreCategoria) {
        return productoRepository.findByCategoriaNombre(nombreCategoria).stream()
                .map(ProductoResponse::desde)
                .toList();
    }

    // Version optimizada: una sola consulta con INNER JOIN.
    @Transactional(readOnly = true)
    public List<ProductoResponse> buscarPorCategoriaConFetch(String nombreCategoria) {
        return productoRepository.buscarPorCategoriaConFetch(nombreCategoria).stream()
                .map(ProductoResponse::desde)
                .toList();
    }

    // --- Relacion N:N (Producto <-> Etiqueta) ---

    @Transactional
    public ProductoResponse agregarEtiqueta(Long productoId, Long etiquetaId) {
        Producto producto = buscarEntidadPorId(productoId);
        Etiqueta etiqueta = etiquetaRepository.findById(etiquetaId)
                .orElseThrow(() -> new IllegalArgumentException("No existe la etiqueta " + etiquetaId));
        producto.agregarEtiqueta(etiqueta);
        return ProductoResponse.desde(productoRepository.save(producto));
    }

    // --- Relacion 1:1 (Producto <-> DetalleProducto) ---

    public DetalleProducto crearDetalle(Long productoId, DetalleProducto detalle) {
        Producto producto = buscarEntidadPorId(productoId);
        detalle.setProducto(producto);
        return detalleProductoRepository.save(detalle);
    }

    // --- Paginacion y orden ---

    public Page<ProductoResponse> buscarPorPrecioMaximo(double precioMaximo, Pageable pageable) {
        return productoRepository.findByPrecioLessThanEqual(precioMaximo, pageable)
                .map(ProductoResponse::desde);
    }

    // --- Joins explicitos (JPQL, SQL nativo, projection) ---

    public List<ProductoResponse> buscarPorCategoriaYEtiqueta(String categoria, String etiqueta) {
        return productoRepository.buscarPorCategoriaYEtiqueta(categoria, etiqueta).stream()
                .map(ProductoResponse::desde)
                .toList();
    }

    public List<ProductoResponse> buscarPorCategoriaYEtiquetaNativo(String categoria, String etiqueta) {
        return productoRepository.buscarPorCategoriaYEtiquetaNativo(categoria, etiqueta).stream()
                .map(ProductoResponse::desde)
                .toList();
    }

    public List<ProductoResumen> resumenDeProductos() {
        return productoRepository.resumenDeProductos();
    }
}
