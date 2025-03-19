package com.opdinna.error_vault.backend.Controller;

import com.google.api.client.auth.openidconnect.IdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.opdinna.error_vault.backend.model.domain.*;
import com.opdinna.error_vault.backend.repository.*;
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
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;

@RestController
@RequestMapping("/api")
public class LoginController {

    private static final int JWT_EXPIRY = 15 * 60;
    private static final int REFRESH_TOKEN_EXPIRY = 7 * 24 * 60 * 60;

    @Autowired
    private UserService userService;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String CLIENT_ID;

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @PostMapping("/tokens")
    public ResponseEntity<?> handleLogin(@RequestBody JSONObject credentialResponse,
                                         HttpServletRequest request,
                                         HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        // First try token-based auth if cookies exist
        if (cookies != null && credentialResponse == null) {
            // First check JWT
            Optional<Cookie> jwtCookie = Arrays.stream(cookies)
                    .filter(c -> "jwt".equals(c.getName()))
                    .findFirst();

            if (jwtCookie.isPresent()) {
                try {
                    String jwt = jwtCookie.get().getValue();
                    if (jwtUtils.validateJwtToken(jwt)) {
                        // JWT is valid, no need to do anything
                        return ResponseEntity.ok().body("Token still valid");
                    }
                } catch (ExpiredJwtException e) {
                    // JWT expired, now check refresh token
                    Optional<Cookie> refreshCookie = Arrays.stream(cookies)
                            .filter(c -> "refresh-token".equals(c.getName()))
                            .findFirst();

                    if (refreshCookie.isPresent()) {
                        String refreshToken = refreshCookie.get().getValue();
                        try {
                            if (jwtUtils.validateRefreshToken(refreshToken)) {
                                // Generate new JWT
                                RefreshToken token = refreshTokenRepository.findByToken(refreshToken)
                                        .orElseThrow(() -> new RuntimeException("Refresh token not found"));
                                return generateTokenResponse(token.getUser(), false);
                            }
                        } catch (Exception ex) {
                            logger.error("Error validating refresh token", ex);
                            // Clear invalid cookies
                            return ResponseEntity.ok()
                                    .header(HttpHeaders.SET_COOKIE, clearCookies())
                                    .body("Please login again");
                        }
                    }
                }
            }
        }

        // If we get here, either no valid tokens or Google credentials provided
        if (credentialResponse == null) {
            return ResponseEntity.badRequest().body("No authentication provided");
        }
        return googleLogin(credentialResponse);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return ResponseEntity.badRequest().body("No cookies present");
        }

        Optional<Cookie> refreshCookie = Arrays.stream(cookies)
                .filter(c -> "refresh-token".equals(c.getName()))
                .findFirst();

        if (refreshCookie.isEmpty()) {
            return ResponseEntity.badRequest().body("No refresh token cookie found");
        }

        String refreshToken = refreshCookie.get().getValue();
        try {
            if (jwtUtils.validateRefreshToken(refreshToken)) {
                RefreshToken token = refreshTokenRepository.findByToken(refreshToken)
                        .orElseThrow(() -> new RuntimeException("Refresh token not found"));

                // Delete old refresh token
                refreshTokenRepository.delete(token);

                // Generate new tokens with rotation
                return generateTokenResponse(token.getUser(), true);
            } else {
                return ResponseEntity.badRequest()
                        .header(HttpHeaders.SET_COOKIE, clearCookies())
                        .body("Invalid refresh token");
            }
        } catch (Exception e) {
            logger.error("Error refreshing token", e);
            return ResponseEntity.badRequest()
                    .header(HttpHeaders.SET_COOKIE, clearCookies())
                    .body("Session expired");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            Optional<Cookie> refreshCookie = Arrays.stream(cookies)
                    .filter(c -> "refresh-token".equals(c.getName()))
                    .findFirst();

            refreshCookie.flatMap(cookie -> refreshTokenRepository.findByToken(cookie.getValue())).ifPresent(refreshTokenRepository::delete);
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, clearCookies())
                .body("Logged out successfully");
    }

    private ResponseEntity<?> googleLogin(JSONObject credentialResponse) {
        try {
            String idTokenString = credentialResponse.getAsString("credential");
            if (idTokenString == null) {
                return ResponseEntity.badRequest().body("No Google credential provided");
            }

            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                    .setAudience(Collections.singletonList(CLIENT_ID))
                    .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken == null) {
                return ResponseEntity.badRequest().body("Invalid Google ID token");
            }

            Payload payload = idToken.getPayload();
            String email = (String) payload.get("email");
            String username = (String) payload.get("name");

            User user = userService.getUser(email);
            if (user == null) {
                user = createNewUser(username, email);
            }

            return generateTokenResponse(user, true);

        } catch (IOException | GeneralSecurityException e) {
            logger.error("Error during authentication", e);
            return ResponseEntity.internalServerError().body("Authentication failed");
        }
    }

    private User createNewUser(String username, String email) {
        User user = new User(username, email);
        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        roles.add(userRole);
        user.setRoles(roles);
        return userService.addUser(user);
    }

    private ResponseEntity<?> generateTokenResponse(User user, boolean newRefreshToken) {
        String jwt = jwtUtils.generateJwtToken(user.getEmail());
        ResponseCookie jwtCookie = ResponseCookie.from("jwt", jwt)
                .httpOnly(true)
                .sameSite("Strict")
                .secure(true)
                .maxAge(JWT_EXPIRY)
                .path("/")
                .build();

        List<String> cookies = new ArrayList<>();
        cookies.add(jwtCookie.toString());

        if (newRefreshToken) {
            String refreshToken = jwtUtils.generateNewRefreshToken(user).getToken();
            ResponseCookie refreshTokenCookie = ResponseCookie.from("refresh-token", refreshToken)
                    .httpOnly(true)
                    .sameSite("Strict")
                    .secure(true)
                    .maxAge(REFRESH_TOKEN_EXPIRY)
                    .path("/")
                    .build();
            cookies.add(refreshTokenCookie.toString());
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookies.toArray(new String[0]))
                .body("Authentication successful");
    }

    private String[] clearCookies() {
        ResponseCookie jwtCookie = ResponseCookie.from("jwt", "")
                .httpOnly(true)
                .sameSite("Strict")
                .secure(true)
                .maxAge(0)
                .path("/")
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refresh-token", "")
                .httpOnly(true)
                .sameSite("Strict")
                .secure(true)
                .maxAge(0)
                .path("/")
                .build();

        return new String[]{jwtCookie.toString(), refreshCookie.toString()};
    }
}