package com.magadiflo.app.config;

import com.magadiflo.app.dao.UserDao;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JwtAuthFilter
 * *************
 * Para decirle a Spring que esta clase será una clase de filtro,
 * es decir una clase de filtro personalizada donde la usaremos
 * para la autenticación con JWT, necesitamos extender de una clase
 * de filtro: OncePerRequestFilter.
 * Eso significa que cada solicitud que se haga a nuestro backend
 * primero será interceptado por ese filtro.
 * <p>
 * Debemos implementar el método doFilterInternal(...) del filtro para
 * decirle a Spring lo que debemos hacer cuando interceptamos la solicitud.
 */

/**
 * Decirle a Spring Security que use este Filtro
 * **********************************************
 * Debemos decirle a Spring que use este filtro para
 * filtrar todas las solicitudes entrantes a nuestra aplicación.
 * Para eso debemos utilizar la clase SecurityConfig y en el método
 * securityFilterChain(...) agregar este filtro. Pero este filtro
 * lo debemos agregar antes de otro filtro, para eso usamos el
 * addFilterBefore().
 *
 * Nuestro filtro debe estar antes del filtro
 * UsernamePasswordAuthenticationFilter.class
 */
@RequiredArgsConstructor
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final UserDao userDao;
    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String userEmail;
        final String jwtToken;
        final String PREFIX = "Bearer ";

        if (authHeader == null || !authHeader.startsWith(PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        jwtToken = authHeader.substring(PREFIX.length());
        userEmail = this.jwtUtil.extractUsername(jwtToken);

        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDao.findUserByEmail(userEmail);

            if (this.jwtUtil.isTokenValid(jwtToken, userDetails)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}
