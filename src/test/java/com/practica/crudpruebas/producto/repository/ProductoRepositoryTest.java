package com.practica.crudpruebas.producto.repository;

import com.practica.crudpruebas.categoria.model.Categoria;
import com.practica.crudpruebas.producto.dto.ProductoResumen;
import com.practica.crudpruebas.producto.model.Producto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

// @DataJpaTest levanta SOLO la capa de persistencia (Hibernate + los
// repositorios), no arranca Controllers ni Services -- y usa, por defecto,
// una base de datos EN MEMORIA (aca, la misma H2 que usamos en dev).
// Cada @Test corre en su propia transaccion, con ROLLBACK automatico al
// final -- los datos de un test nunca contaminan al siguiente.
@DataJpaTest
@ActiveProfiles("test") // usa application-test.yaml (ddl-auto: create-drop)
class ProductoRepositoryTest {

    @Autowired
    private ProductoRepository productoRepository;

    // Version simplificada del EntityManager, pensada para preparar datos
    // de prueba directo, sin pasar por el repositorio que estamos probando.
    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findByNombreContainingIgnoreCase_encuentraSinImportarMayusculas() {
        entityManager.persist(new Producto(null, "Mouse Gamer", 25.0, 10));
        entityManager.persist(new Producto(null, "Teclado Mecanico", 60.0, 5));

        List<Producto> encontrados = productoRepository.findByNombreContainingIgnoreCase("MOUSE");

        assertThat(encontrados).hasSize(1);
        assertThat(encontrados.get(0).getNombre()).isEqualTo("Mouse Gamer");
    }

    @Test
    void buscarPorCategoriaConFetch_traeElProductoYSuCategoriaEnUnaSolaConsulta() {
        Categoria categoria = entityManager.persist(new Categoria(null, "Perifericos"));
        Producto producto = new Producto(null, "Mouse Gamer", 25.0, 10);
        producto.setCategoria(categoria);
        entityManager.persistAndFlush(producto);

        List<Producto> resultado = productoRepository.buscarPorCategoriaConFetch("Perifericos");

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getCategoria().getNombre()).isEqualTo("Perifericos");
    }

    @Test
    void resumenDeProductos_proyectaSoloLosCamposPedidos() {
        Categoria categoria = entityManager.persist(new Categoria(null, "Perifericos"));
        Producto producto = new Producto(null, "Mouse Gamer", 25.0, 10);
        producto.setCategoria(categoria);
        entityManager.persistAndFlush(producto);

        List<ProductoResumen> resumen = productoRepository.resumenDeProductos();

        assertThat(resumen).hasSize(1);
        assertThat(resumen.get(0).getNombre()).isEqualTo("Mouse Gamer");
        assertThat(resumen.get(0).getCategoriaNombre()).isEqualTo("Perifericos");
    }
}
