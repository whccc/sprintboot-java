package com.practica.crudpruebas.common.exception;

import com.practica.crudpruebas.producto.exception.ProductoNoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

// @RestControllerAdvice es GLOBAL: intercepta excepciones de CUALQUIER
// controlador de la app (Producto, Categoria, Etiqueta, los que vengan).
// Por eso vive en "common" y no dentro del paquete de un feature puntual.
@RestControllerAdvice
public class ManejadorDeErrores {

    @ExceptionHandler(ProductoNoEncontradoException.class)
    public ResponseEntity<Map<String, String>> manejarNoEncontrado(ProductoNoEncontradoException ex) {
        Map<String, String> body = new HashMap<>();
        body.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    // Misma idea, para la excepcion equivalente del lado hexagonal --
    // vive en un paquete de dominio distinto, pero el manejador global
    // sigue siendo UNO SOLO para toda la app.
    @ExceptionHandler(com.practica.crudpruebas.hexagonal.producto.domain.ProductoNoEncontradoException.class)
    public ResponseEntity<Map<String, String>> manejarNoEncontradoHexagonal(
            com.practica.crudpruebas.hexagonal.producto.domain.ProductoNoEncontradoException ex) {
        Map<String, String> body = new HashMap<>();
        body.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> manejarValidacion(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errores.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errores);
    }
}
