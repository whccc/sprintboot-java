package com.practica.crudpruebas.hexagonal.pedido.application;

import com.practica.crudpruebas.hexagonal.categoria.domain.Categoria;
import com.practica.crudpruebas.hexagonal.categoria.domain.CategoriaRepositorio;
import com.practica.crudpruebas.hexagonal.pedido.domain.Pedido;
import com.practica.crudpruebas.hexagonal.pedido.domain.PedidoRepositorio;
import com.practica.crudpruebas.hexagonal.pedido.domain.ValidadorRestriccionCategoria;
import com.practica.crudpruebas.hexagonal.producto.domain.Producto;
import com.practica.crudpruebas.hexagonal.producto.domain.ProductoRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Este Service toca 3 entidades de 2 features DISTINTOS del arbol hexagonal
// (Producto y Categoria pertenecen a "producto"/"categoria", Pedido es SUYO).
// Nada raro en eso -- el Service de un caso de uso puede depender de los
// puertos de OTROS features, siempre y cuando dependa de la interfaz de
// dominio (puerto), nunca de la implementacion JPA de otro feature.
//
// Notese el reparto de trabajo ahora: este Service SOLO busca (via puertos)
// y orquesta el orden de los pasos. La DECISION de si la cantidad pedida
// viola la restriccion de categoria ya no esta aca -- se la delega a
// ValidadorRestriccionCategoria (Servicio de Dominio), que es puro y
// testeable sin mocks.
//
// OJO: validadorRestriccionCategoria NO se inyecta -- se instancia con
// "new" (mas abajo). Solo se INYECTAN los puertos (Repositorio) porque
// SON interfaces con una implementacion real que Spring tiene que resolver
// (JPA, en este caso). Un Servicio de Dominio sin dependencias no necesita
// que el contenedor de Spring lo administre -- es un simple "new", como
// cualquier otro objeto Java.
@Service
public class PedidoService {

    private final PedidoRepositorio pedidoRepositorio;
    private final ProductoRepositorio productoRepositorio;
    private final CategoriaRepositorio categoriaRepositorio;
    private final ValidadorRestriccionCategoria validadorRestriccionCategoria = new ValidadorRestriccionCategoria();

    public PedidoService(PedidoRepositorio pedidoRepositorio,
                          ProductoRepositorio productoRepositorio,
                          CategoriaRepositorio categoriaRepositorio) {
        this.pedidoRepositorio = pedidoRepositorio;
        this.productoRepositorio = productoRepositorio;
        this.categoriaRepositorio = categoriaRepositorio;
    }

    // OJO con este @Transactional: es una excepcion consciente a "el Service
    // no tiene @Transactional" que vimos con Producto/Categoria/Etiqueta.
    // Ahi no hacia falta porque cada caso de uso tocaba UN solo agregado, y
    // cada metodo del puerto ya es atomico por si solo. ACA si hace falta,
    // porque el caso de uso escribe en VARIOS agregados (N productos + 1
    // pedido) y TODOS tienen que confirmarse juntos o ninguno. El limite de
    // la transaccion es el CASO DE USO, no un metodo de repositorio aislado
    // -- por eso @Transactional vive en el Service cuando cruza agregados.
    @Transactional
    public PedidoResponse crearPedido(PedidoRequest request) {
        Pedido pedido = new Pedido();

        for (ItemPedidoRequest itemRequest : request.items()) {
            Producto producto = productoRepositorio.buscarPorId(itemRequest.productoId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "No existe el producto " + itemRequest.productoId()));

            // El Service se limita a RESOLVER la categoria (necesita el puerto).
            // La DECISION de si esto viola la regla la toma el Domain Service.
            Categoria categoria = producto.getCategoriaId() != null
                    ? categoriaRepositorio.buscarPorId(producto.getCategoriaId()).orElse(null)
                    : null;
            validadorRestriccionCategoria.validar(producto, categoria, itemRequest.cantidad());

            // Si no alcanza el stock, Producto.descontarStock() tira
            // IllegalArgumentException -- sale del for, sale del metodo, y
            // @Transactional deshace TODO lo que ya se guardo en vueltas
            // anteriores del loop (el stock de productos previos incluido).
            producto.descontarStock(itemRequest.cantidad());
            productoRepositorio.guardar(producto);

            pedido.agregarItem(producto.getId(), producto.getNombre(),
                    itemRequest.cantidad(), producto.getPrecio());
        }

        Pedido guardado = pedidoRepositorio.guardar(pedido);
        return aResponse(guardado);
    }

    @Transactional(readOnly = true)
    public PedidoResponse buscarPorId(Long id) {
        Pedido pedido = pedidoRepositorio.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe el pedido " + id));
        return aResponse(pedido);
    }

    private PedidoResponse aResponse(Pedido pedido) {
        var items = pedido.getItems().stream()
                .map(item -> new ItemPedidoResponse(
                        item.getProductoId(), item.getNombreProducto(),
                        item.getCantidad(), item.getPrecioUnitario(), item.subtotal()))
                .toList();
        return new PedidoResponse(pedido.getId(), items, pedido.calcularTotal(), pedido.tieneDescuentoAplicado());
    }
}
