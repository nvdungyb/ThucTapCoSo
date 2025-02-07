package com.shopme.security.jwt;

import com.shopme.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class AuthJwtTokenFilter extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;
    private final UserDetailsServiceImpl customerDetailsService;

    public AuthJwtTokenFilter(JwtUtils jwtUtils, UserDetailsServiceImpl customerDetailsService) {
        this.jwtUtils = jwtUtils;
        this.customerDetailsService = customerDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwtToken = parse(request);
            if (jwtToken != null && jwtUtils.validateJwtToken(jwtToken, jwtUtils.getAccessTokenSecret())) {
                String email = jwtUtils.getEmailFromAccessToken(jwtToken);

                UserDetails customerDetails = customerDetailsService.loadUserByUsername(email);
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(customerDetails, null, customerDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        } catch (Exception e) {
            logger.error("Can not set user authentication: {}" + e);
        }

        filterChain.doFilter(request, response);
    }

    private String parse(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        String jwtToken = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwtToken = authHeader.substring(7);
        }
        return jwtToken;
    }
}
