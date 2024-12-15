package com.opdinna.error_vault.backend.security.jwt;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.opdinna.error_vault.backend.security.services.UserDetailsServiceImpl;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AuthTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        String email = "";
        String path = request.getRequestURI();
        if (path.startsWith("/api/")) {
            try {
                filterChain.doFilter(request, response);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ServletException e) {
                e.printStackTrace();
            }
            return;
        }
        try {
            // Get all cookies
            Cookie[] cookies = request.getCookies();

            String jwt = "", refreshToken = "";

            // Retrieve jwt and refresh tokens from cookies
            for (Cookie c : cookies) {
                if (c != null && "jwt".equals(c.getName())) {
                    jwt = c.getValue();
                } else if (c != null && "refreshToken".equals(c.getName())) {
                    refreshToken = c.getValue();
                }
            }
            System.out.println("JWT cookie is " + jwt);
            System.out.println("Refresh cookie is " + refreshToken);

            boolean validJwtToken = false;

            if (jwt != null) {

                try {
                    validJwtToken = jwtUtils.validateJwtToken(jwt);
                } catch (ExpiredJwtException e) {
                    // Return a response entity that the jwt is expired
                    logger.info("The received jwt token is expired");
                    response.sendError(421, "The received jwt token is expired");
                    return;
                }

                // Case of valid jwt
                if (validJwtToken) {
                    email = jwtUtils.getEmailFromJwtToken(jwt);

                    UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());

                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            } else {
                logger.error("Invalid Token or user not registered");
                throw new Exception();
            }

            filterChain.doFilter(request, response);
        } catch (UsernameNotFoundException e) {
            logger.error("Cannot set user with given email {}", email);
        } catch (Exception e) {
            logger.error("Cannot set user authentication: {}", e);
        }
    }

    private String parseJwt(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        return null;
    }

}
