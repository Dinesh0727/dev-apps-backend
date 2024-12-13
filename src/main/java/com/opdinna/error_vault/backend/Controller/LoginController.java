package com.opdinna.error_vault.backend.Controller;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.api.client.auth.openidconnect.IdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.opdinna.error_vault.backend.model.domain.ERole;
import com.opdinna.error_vault.backend.model.domain.JwtResponse;
import com.opdinna.error_vault.backend.model.domain.Role;
import com.opdinna.error_vault.backend.model.domain.User;
import com.opdinna.error_vault.backend.repository.RoleRepository;
import com.opdinna.error_vault.backend.repository.UserRepository;
import com.opdinna.error_vault.backend.security.jwt.JwtUtils;
import com.opdinna.error_vault.backend.security.services.UserDetailsImpl;
import com.opdinna.error_vault.backend.service.UserService;

import net.minidev.json.JSONObject;

@RestController
@RequestMapping("/api")

public class LoginController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String CLIENT_ID;

    @PostMapping("/tokens")
    public ResponseEntity<?> postMethodName(@RequestBody JSONObject credentialResponse) {

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

                String jwt = jwtUtils.generateJwtToken(email);

                List<String> rolesList = user.getRoles().stream()
                        .map(role -> role.getName().toString())
                        .collect(Collectors.toList());

                return ResponseEntity.ok(new JwtResponse(jwt, user.getId(), username, email, rolesList));
            } else {
                return ResponseEntity.badRequest().body("Invalid ID token");
            }
        } catch (IOException | GeneralSecurityException e) {
            return ResponseEntity.internalServerError().body("Error during token verification: " + e.getMessage());
        }
    }

}
