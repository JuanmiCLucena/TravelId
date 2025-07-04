package com.eoi.grupo5.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;


import java.io.IOException;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {

        String errorMessage = "";

        if (exception.getMessage().contains("Bad credentials")) {
            errorMessage = "Nombre de usuario o contraseña incorrectos.";
        } else {
            errorMessage = "Error de autenticación.";
        }

        request.getSession().setAttribute("errorMessage", errorMessage);

        response.sendRedirect("/login");
    }
}
