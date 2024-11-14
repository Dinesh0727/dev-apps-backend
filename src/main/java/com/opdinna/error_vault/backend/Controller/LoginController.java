package com.opdinna.error_vault.backend.Controller;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.api.client.auth.openidconnect.IdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.opdinna.error_vault.backend.model.domain.User;
import com.opdinna.error_vault.backend.service.UserService;

import net.minidev.json.JSONObject;

@RestController
@RequestMapping("/api")

public class LoginController {

    @Autowired
    private UserService userService;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String CLIENT_ID;

    @PostMapping("/tokens")
    public String postMethodName(@RequestBody JSONObject credentialResponse) {

        String idTokenString = credentialResponse.getAsString("credential");

        // Regarding the NetHttpTransport and GsonFactory : https://tinyurl.com/ykv7cp6y
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(CLIENT_ID)).build();

        GoogleIdToken idToken;

        try {
            idToken = verifier.verify(idTokenString);

            if (idToken != null) {
                Payload payload = idToken.getPayload();

                String username = (String) payload.get("name");
                String email = (String) payload.get("email");
                String pictureUrl = (String) payload.get("picture");

                System.out.println("Email Address of test email: " + email);
                System.out.println("User Name of test email: " + username);
                System.out.println("Picture Url of test email: " + pictureUrl);

                callToDataBase(username, email);

                return email;
            } else {
                System.out.println("Invalid ID token.");
                return "Invalid ID token.";
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed during verification of email: " + e.getMessage(), e);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException("Security issue during ID token verification: " + e.getMessage(), e);
        }

    }

    private void callToDataBase(String username, String email) {

        User user = userService.getUser(email);

        if(user == null){
            user = new User(username, email);
        }
        
        userService.addUser(user);
    }

}
