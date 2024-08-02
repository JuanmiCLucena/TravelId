package com.eoi.grupo5.security;

import java.io.IOException;
import java.util.Set;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

/**
 * Manejador de éxito personalizado para la autenticación.
 * <p>
 * Esta clase se encarga de redirigir a los usuarios a diferentes páginas según su rol
 * después de un inicio de sesión exitoso.
 * </p>
 */
@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    /**
     * Maneja la redirección después de un inicio de sesión exitoso.
     *
     * @param request La solicitud HTTP.
     * @param response La respuesta HTTP.
     * @param authentication La información de autenticación del usuario.
     * @throws IOException Si ocurre un error de entrada/salida durante la redirección.
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        // Obtiene los roles del usuario autenticado.
        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());

        // Verifica si el usuario tiene el rol "ADMIN".
        if (roles.contains("ADMIN")) {
            // Redirige a la página de inicio de administrador si el rol es "ADMIN".
            response.sendRedirect("/admin/inicio");
        } else {
            // Redirige a la página principal para todos los demás usuarios.
            response.sendRedirect("/");
        }
    }
}
