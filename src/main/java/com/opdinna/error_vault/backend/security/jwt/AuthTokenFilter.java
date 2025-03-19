package com.opdinna.error_vault.backend.security.jwt;

import com.opdinna.error_vault.backend.security.services.UserDetailsServiceImpl;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

public class AuthTokenFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);
    private static final String JWT_COOKIE_NAME = "jwt";
    private static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";
    private static final String API_PATH_PREFIX = "/api/";

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(@Nullable  HttpServletRequest request, @Nullable HttpServletResponse response, @Nullable FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // Skip authentication for API paths
            if (request != null && isApiPath(request) && filterChain != null) {
                filterChain.doFilter(request, response);
                return;
            }

            processAuthentication(request, response, filterChain);
        } catch (Exception e) {
            logger.error("Authentication error: ", e);
            sendAuthenticationError(response, "Authentication failed");
        }
    }

    private boolean isApiPath(HttpServletRequest request) {
        return request.getRequestURI().startsWith(API_PATH_PREFIX);
    }

    private void processAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            Optional<String> jwtToken = extractCookieValue(request);

            if (jwtToken.isEmpty()) {
                logger.warn("No JWT token found in cookies");
                sendAuthenticationError(response, "No JWT token provided");
                return;
            }

            processJwtToken(jwtToken.get(), request, response, filterChain);
        } catch (ExpiredJwtException e) {
            logger.info("JWT token is expired");
            sendAuthenticationError(response, "JWT token expired");
        }
    }

    private void processJwtToken(String jwt, HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (!jwtUtils.validateJwtToken(jwt)) {
            logger.warn("Invalid JWT token");
            sendAuthenticationError(response, "Invalid JWT token");
            return;
        }

        String email = jwtUtils.getEmailFromJwtToken(jwt);
        authenticateUser(email, request);
        filterChain.doFilter(request, response);
    }

    private void authenticateUser(String email, HttpServletRequest request) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private Optional<String> extractCookieValue(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return Optional.empty();
        }

        return Arrays.stream(cookies)
                .filter(cookie -> cookie != null && AuthTokenFilter.JWT_COOKIE_NAME.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst();
    }

    private void sendAuthenticationError(HttpServletResponse response, String message)
            throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.sendError(HttpStatus.UNAUTHORIZED.value(), message);
    }
}