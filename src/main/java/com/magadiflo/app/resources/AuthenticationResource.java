package com.magadiflo.app.resources;

import com.magadiflo.app.config.JwtUtil;
import com.magadiflo.app.dao.UserDao;
import com.magadiflo.app.dto.AuthenticationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/v1/auth")
public class AuthenticationResource {

    private final AuthenticationManager authenticationManager;
    private final UserDao userDao;

    private final JwtUtil jwtUtil;

    @PostMapping(path = "/authenticate")
    public ResponseEntity<String> authenticate(@RequestBody AuthenticationRequest request) {
        // Cada vez que intentemos autenticar a un usuario, delegaremos esa responsabilidad a Spring
        // Él realizará el proceso  y llamará al userDetailsService, y así sucesivamente hasta autenticar al usuario.
        // Para esto, es necesario tener una implementación o un @Bean de la interfaz AuthenticationManager
        this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        final UserDetails userDetails = this.userDao.findUserByEmail(request.getEmail());
        if(userDetails != null) {
            return ResponseEntity.ok(jwtUtil.generateToken(userDetails));
        }
        return ResponseEntity.badRequest().body("Some error has ocurred");
    }

}
