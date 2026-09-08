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

    // --- Demo: que hace EXACTAMENTE readOnly (dirty checking) ---
    //
    // OJO: a proposito NO llamamos a productoRepository.save() en ninguno de
    // los 2 metodos de abajo. La pregunta es: ¿se guarda el cambio igual?

    // SIN readOnly: Hibernate trackea la entidad ("dirty checking"). Al hacer
    // commit, compara el estado actual contra el original y, si difiere,
    // genera el UPDATE el solo -- aunque nunca hayas llamado a save().
    @Transactional
    public void demoSinReadOnly(Long id, String nuevoNombre) {
        Producto producto = buscarEntidadPorId(id);
        producto.setNombre(nuevoNombre);
        // sin save() -- y aun asi se va a guardar
    }

    // CON readOnly: Hibernate NO trackea cambios en las entidades cargadas.
    // El setNombre() de aca abajo se pierde apenas termina el metodo --
    // nunca se genera ningun UPDATE, ni con dirty checking ni sin el.
    @Transactional(readOnly = true)
    public void demoConReadOnly(Long id, String nuevoNombre) {
        Producto producto = buscarEntidadPorId(id);
        producto.setNombre(nuevoNombre);
        // este cambio se descarta -- readOnly se lo impide a Hibernate
    }

    // FLUJO MIXTO: un paso de lectura (buscarEntidadPorId) + una escritura
    // EXPLICITA (save(), no dirty checking implicito). Todo el metodo esta
    // marcado readOnly = true. La pregunta: ¿el save() explicito se salva de
    // la restriccion? -- @Transactional es UNA sola unidad, no se puede tener
    // "una parte" readOnly y "otra parte" no, DENTRO del mismo metodo.
    @Transactional(readOnly = true)
    public void demoFlujoMixto(Long id, String nuevoNombre) {
        Producto producto = buscarEntidadPorId(id); // esto SI es "solo obtener"
        producto.setNombre(nuevoNombre);
        productoRepository.save(producto); // escritura EXPLICITA, no implicita
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

    // --- Ejemplo de ATOMICIDAD real: 2 escrituras que deben pasar juntas ---
    //
    // @Transactional (sin readOnly) porque escribimos en 2 filas distintas.
    // Si la segunda escritura falla, Spring hace ROLLBACK de TODO el metodo --
    // incluida la resta de stock del producto origen que ya habiamos hecho
    // (aunque ese cambio siga "flotando" en memoria/en la sesion, nunca llega
    // a la base de datos con COMMIT).
    @Transactional
    public void transferirStock(Long idOrigen, Long idDestino, int cantidad) {
        Producto origen = buscarEntidadPorId(idOrigen);
        if (origen.getStock() < cantidad) {
            throw new IllegalArgumentException("Stock insuficiente en el producto origen");
        }
        origen.setStock(origen.getStock() - cantidad);
        productoRepository.save(origen);

        // Si idDestino no existe, esto tira ProductoNoEncontradoException DESPUES
        // de haber "guardado" el origen -- pero como estamos en la MISMA
        // transaccion, ese guardado nunca llega a confirmarse en la base.
        Producto destino = buscarEntidadPorId(idDestino);
        destino.setStock(destino.getStock() + cantidad);
        productoRepository.save(destino);
    }

    // VERSION PELIGROSA: en vez de lanzar una excepcion cuando algo sale mal,
    // "informa" el fallo devolviendo false. @Transactional NO mira el valor
    // que devuelve el metodo -- solo reacciona a excepciones. Si no lanzas
    // nada, Spring interpreta que el metodo termino BIEN y hace COMMIT de
    // lo que ya se alcanzo a guardar, aunque el flujo logico haya "fallado".
    @Transactional
    public boolean transferirStockSinExcepcion(Long idOrigen, Long idDestino, int cantidad) {
        Producto origen = buscarEntidadPorId(idOrigen);
        if (origen.getStock() < cantidad) {
            return false;
        }
        origen.setStock(origen.getStock() - cantidad);
        productoRepository.save(origen); // esto SI se va a confirmar, aunque el metodo "falle" despues

        Producto destino = productoRepository.findById(idDestino).orElse(null);
        if (destino == null) {
            return false; // "fallo", pero el metodo termina NORMAL -- no hay rollback
        }
        destino.setStock(destino.getStock() + cantidad);
        productoRepository.save(destino);
        return true;
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
