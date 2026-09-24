package com.practica.crudpruebas.common.exception;

import com.practica.crudpruebas.producto.exception.ProductoNoEncontradoException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
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

    private static final Logger log = LoggerFactory.getLogger(ManejadorDeErrores.class);

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

    // Misma idea, para la excepcion equivalente del lado Clean Architecture
    // -- vive en entities/, tercer paquete de dominio distinto, mismo
    // manejador global unico para toda la app.
    @ExceptionHandler(com.practica.crudpruebas.clean.producto.entities.ProductoNoEncontradoException.class)
    public ResponseEntity<Map<String, String>> manejarNoEncontradoClean(
            com.practica.crudpruebas.clean.producto.entities.ProductoNoEncontradoException ex) {
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

    // Login con usuario/clave incorrectos -- lo tira
    // authenticationManager.authenticate() dentro de AuthController.login().
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, String>> manejarAutenticacion(AuthenticationException ex,
                                                                      HttpServletRequest request) {
        // WARN, no ERROR -- un login fallido es un evento de seguridad
        // esperable (alguien se equivoco de clave), no un bug de la app.
        // NUNCA se loguea el body del request ni la clave -- solo la IP de
        // origen y el mensaje de la excepcion (que ya viene sin la clave).
        log.warn("Intento de autenticacion fallido desde {}: {}", request.getRemoteAddr(), ex.getMessage());

        Map<String, String> body = new HashMap<>();
        body.put("error", "Usuario o clave incorrectos");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    // El usuario SI esta autenticado (el token es valido), pero no tiene el
    // ROL necesario -- lo tira @PreAuthorize cuando la condicion da false.
    // 401 = "no se quien sos". 403 = "se quien sos, pero no podes hacer esto".
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> manejarAccesoDenegado(AccessDeniedException ex) {
        // Aca SI podemos loguear quien fue -- a diferencia del 401 de
        // arriba, este usuario esta autenticado (tiene un token valido),
        // solo le falta el rol. Util para detectar patrones de abuso.
        String usuario = SecurityContextHolder.getContext().getAuthentication().getName();
        log.warn("Acceso denegado para el usuario '{}': {}", usuario, ex.getMessage());

        Map<String, String> body = new HashMap<>();
        body.put("error", "No tenes permiso para esta operacion");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }
}
