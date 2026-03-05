package com.mpp.forms.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.forms.v1.FormsScopes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Service
@Slf4j
public class GoogleAuthenticationService {
    private final TelegramMessagingService twillioService;
    @Value("${google.drive.client.id}")
    private String momsClientId;
    @Value("${google.drive.client.secret}")
    private String momsClientSecret;

    private String token;

    public GoogleAuthenticationService(TelegramMessagingService twillioService) {
        this.twillioService = twillioService;
    }

    public String getGoogleAppCredentials() {
        log.info("Initializing Google Authorization Flow");
        try {
            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    GsonFactory.getDefaultInstance(),
                    momsClientId,
                    momsClientSecret,
                    Collections.singleton(FormsScopes.DRIVE))
                    .setDataStoreFactory(new FileDataStoreFactory(new File("classpath:keys/tokens")))
                    .setAccessType("offline")
                    .build();

            String googleAuthorizationUrl = flow.newAuthorizationUrl()
                    .setRedirectUri("${server.address}:${server.port}/oauth/callback")
                    .build();

            return googleAuthorizationUrl;
        } catch (IOException | GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }
}
