package com.practica.crudpruebas.hexagonal.producto.presentacion;

import com.practica.crudpruebas.hexagonal.producto.application.ProductoRequest;
import com.practica.crudpruebas.hexagonal.producto.application.ProductoResponse;
import com.practica.crudpruebas.hexagonal.producto.application.ProductoService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

// SEGUNDO adaptador "driving" (dispara el caso de uso) para el MISMO
// ProductoService que ya usa ProductoController -- pero este entra por
// linea de comandos al arrancar la app, no por HTTP.
//
// Ninguno de los 2 adaptadores sabe que el otro existe. ProductoService
// tampoco sabe (ni le importa) si lo llamo un Controller o un CLI -- es
// la prueba en codigo de por que la carpeta se llama "presentacion" y no
// "web": ambos son formas de "presentar" el sistema hacia afuera.
//
// Se activa SOLO si se pasa el argumento --crear-producto, para no
// interferir con el arranque normal del servidor web (que es lo que pasa
// el 99% de las veces que esta clase se ejecuta).
@Component
public class ProductoCliRunner implements ApplicationRunner {

    private final ProductoService productoService;

    public ProductoCliRunner(ProductoService productoService) {
        this.productoService = productoService;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!args.containsOption("crear-producto")) {
            return;
        }

        ProductoRequest request = parsear(args.getOptionValues("crear-producto").get(0));
        ProductoResponse creado = productoService.crear(request);

        System.out.println("=== Producto creado via CLI (mismo ProductoService que usa el Controller) ===");
        System.out.println("id=" + creado.id() + " nombre=" + creado.nombre() + " precio=" + creado.precio());
    }

    // "Teclado,50.0,10" -> ProductoRequest("Teclado", 50.0, 10, null)
    private ProductoRequest parsear(String valorCrudo) {
        String[] partes = valorCrudo.split(",");
        String nombre = partes[0];
        double precio = Double.parseDouble(partes[1]);
        int stock = Integer.parseInt(partes[2]);
        return new ProductoRequest(nombre, precio, stock, null);
    }
}
