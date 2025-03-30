package com.expenses.tracker.config;

import com.expenses.tracker.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        System.out.println("JWT Filter processing request: " + request.getMethod() + " " + request.getRequestURI());
        
        String authHeader = request.getHeader("Authorization");
        System.out.println("Authorization header: " + authHeader);
        
        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("Invalid authorization header format");
            filterChain.doFilter(request, response);
            return;
        }
        
        String jwtToken = authHeader.substring(7);
        String username = jwtService.extractUsername(jwtToken);
        System.out.println("Extracted username: " + username);
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(username != null && authentication == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            System.out.println("Loaded user details for: " + userDetails.getUsername());
            
            boolean isValid = jwtService.isValidToken(jwtToken, userDetails);
            System.out.println("Token valid: " + isValid);
            
            if(isValid) {
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );
                authenticationToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                System.out.println("Authentication set in SecurityContext");
            }
        }
        
        filterChain.doFilter(request, response);
    }
}
