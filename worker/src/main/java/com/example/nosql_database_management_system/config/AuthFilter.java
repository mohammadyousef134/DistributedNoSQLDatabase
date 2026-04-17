package com.example.nosql_database_management_system.config;

import com.example.nosql_database_management_system.service.AuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class AuthFilter extends OncePerRequestFilter {
    @Autowired
    private AuthService authService;

    @Value("${node.name}")
    private String currentWorker;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        if (path.startsWith("/internal")) {
            filterChain.doFilter(request, response);
            return;
        }

        String username = request.getHeader("username");
        String token = request.getHeader("token");
        if (username == null || token == null ||
                !authService.isValid(username, token)) {
        System.out.println("username=" + username + " token=" + token);

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized");
            return;
        }

        if (!authService.isCorrectWorker(token, currentWorker)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Wrong worker");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
