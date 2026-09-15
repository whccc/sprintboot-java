package com.practica.crudpruebas.common.security;

import com.practica.crudpruebas.usuario.model.Usuario;
import com.practica.crudpruebas.usuario.repository.UsuarioRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

// El puente entre TU tabla de usuarios y lo que Spring Security entiende.
// Spring Security no sabe nada de JPA ni de tu entidad Usuario -- solo
// conoce la interfaz UserDetails. Esta clase traduce una en la otra.
@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("No existe el usuario " + username));

        // "ROLE_" es el prefijo que Spring Security espera para que
        // hasRole("ADMIN") funcione -- sin el prefijo, tendrias que usar
        // hasAuthority("ADMIN") en su lugar. Los 2 existen por razones
        // historicas; ROLE_ + hasRole() es la convencion mas comun.
        return User.builder()
                .username(usuario.getUsername())
                .password(usuario.getPassword())
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRol())))
                .build();
    }
}
