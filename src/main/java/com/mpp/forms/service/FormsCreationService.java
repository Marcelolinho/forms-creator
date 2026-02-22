package com.mpp.forms.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.forms.v1.FormsScopes;
import com.mpp.forms.domain.PromptParamsBo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Service
public class FormsCreationService {

    @Value("classpath:keys/drive_client_id.txt")
    private String momsClientId;
    @Value("classpath:keys/drive_client_secret.txt")
    private String momsClientSecret;
    private String refreshToken;

    public String createForms(PromptParamsBo promptParams) {
        Credential teste = getGoogleAppCredentials();

        if (teste == null) {
            return "deu errado";
        }
        return "deu certo";
    }

    private Credential getGoogleAppCredentials() {
        try {
            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    GsonFactory.getDefaultInstance(),
                    momsClientId,
                    momsClientSecret,
                    Collections.singleton(FormsScopes.DRIVE))
                    // This is where the Refresh Token is saved so she only logs in ONCE
                    .setDataStoreFactory(new FileDataStoreFactory(new File("classpath:keys/tokens")))
                    .setAccessType("offline")
                    .build();

            LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();

            return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
        } catch (IOException | GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }
}
