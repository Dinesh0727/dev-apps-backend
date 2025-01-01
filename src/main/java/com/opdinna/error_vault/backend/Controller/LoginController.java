package com.opdinna.error_vault.backend.Controller;

import com.google.api.client.auth.openidconnect.IdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.opdinna.error_vault.backend.model.domain.ERole;
import com.opdinna.error_vault.backend.model.domain.RefreshToken;
import com.opdinna.error_vault.backend.model.domain.Role;
import com.opdinna.error_vault.backend.model.domain.User;
import com.opdinna.error_vault.backend.repository.RefreshTokenRepository;
import com.opdinna.error_vault.backend.repository.RoleRepository;
import com.opdinna.error_vault.backend.security.jwt.JwtUtils;
import com.opdinna.error_vault.backend.service.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.minidev.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/api")

public class LoginController {

    @Autowired
    private UserService userService;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String CLIENT_ID;

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @PostMapping("/tokens")
    public ResponseEntity<?> postMethodName(@RequestBody JSONObject credentialResponse, HttpServletRequest request,
                                            HttpServletResponse response) {

        String idTokenString = credentialResponse.getAsString("credential");

        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(CLIENT_ID))
                .build();

        try {
            GoogleIdToken idToken = verifier.verify(idTokenString);

            if (idToken != null) {
                Payload payload = idToken.getPayload();

                String username = (String) payload.get("name");
                String email = (String) payload.get("email");

                User user = userService.getUser(email);

                if (user == null) {
                    user = new User(username, email);
                    Set<Role> roles = new HashSet<>();
                    Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                    roles.add(userRole);
                    user.setRoles(roles);
                    userService.addUser(user);
                }

                // Check for the jwt cookie - Can be removed after testing
                if (validJwtTokenInInitialLogin(request.getCookies())) {
                    return ResponseEntity.ok().body("Login successful");
                }

                // Generate the jwt token and return it in response entity
                String jwt = jwtUtils.generateJwtToken(email);
                ResponseCookie jwtCookie = ResponseCookie.from("jwt", jwt).httpOnly(true).sameSite("lax")
                        .secure(false)
                        .maxAge(15 * 60).path("/").build();
                System.out.println(jwtCookie.toString());

                String refreshToken = jwtUtils.generateNewRefreshToken(user).getToken();
                ResponseCookie refreshTokenCookie = ResponseCookie.from("refresh-token", refreshToken).httpOnly(true)
                        .sameSite("lax")
                        .secure(false)
                        .maxAge(15 * 60).path("/api/refresh-token").build();
                System.out.println(refreshTokenCookie.toString());

                return ResponseEntity.ok()
                        .header(HttpHeaders.SET_COOKIE, jwtCookie.toString(), refreshTokenCookie.toString()).build();
            } else {
                return ResponseEntity.badRequest().body("Invalid Google ID token");
            }
        } catch (IOException | GeneralSecurityException e) {
            return ResponseEntity.internalServerError().body("Error during token verification: " + e.getMessage());
        }
    }

    // Can be removed after testing
    boolean validJwtTokenInInitialLogin(Cookie[] cookies) {
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                try {
                    if ("jwt".equals(cookie.getName()) && jwtUtils.validateJwtToken(cookie.getValue())) {
                        logger.info("Valid jwt cookie present in the initial login request");
                        return true;
                    }
                } catch (ExpiredJwtException e) {
                    logger.error("The Jwt token in the initial login has expired");
                    return false;
                }
            }
            logger.info("No jwt cookie present in initial login");
        }
        logger.info("No jwt cookie in initial login request!!!");
        return false;
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String jwt = null, refreshToken = null;

        for (Cookie cookie : request.getCookies()) {
            if ("jwt".equals(cookie.getName())) {
                jwt = cookie.getValue();
            } else if ("refresh-token".equals(cookie.getName())) {
                refreshToken = cookie.getValue();
            }
        }

        // Validate the refresh token
        if (refreshToken != null && jwtUtils.validateRefreshToken(refreshToken)) {
            String email = null;
            RefreshToken token = refreshTokenRepository.findByToken(refreshToken)
                    .orElseThrow(() -> new RuntimeException("No user found with given refresh-token "));
            email = token.getUser().getEmail();

            String newJwtToken = jwtUtils.generateJwtToken(email);

            ResponseCookie responseCookie = ResponseCookie.from("jwt", newJwtToken).httpOnly(true).sameSite("lax")
                    .secure(false)
                    .maxAge(15 * 60).path("/").build();

            logger.info(responseCookie.toString());

            return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, responseCookie.toString()).build();
        } else {
            try {
                response.sendError(422, "Refresh token is expired, logging out!!!");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return ResponseEntity.ok().body("Refresh token is expired, logging out!!!");
        }
    }

}
