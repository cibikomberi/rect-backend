package com.rect.iot.filter;

import java.io.IOException;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RedirectUrlFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        String redirectUrl = request.getParameter("redirectUrl");
        if (redirectUrl != null && request.getRequestURI().contains("/oauth2/authorization/")) {
            // Store the redirectUrl in the session
            request.getSession().setAttribute("redirectUrl", redirectUrl);
        }
        filterChain.doFilter(request, response);
    }
}
