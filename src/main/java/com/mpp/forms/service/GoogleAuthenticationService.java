package com.mpp.forms.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.forms.v1.FormsScopes;
import com.mpp.forms.configuration.NgrokConfig;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Collections;

@Service
@Slf4j
public class GoogleAuthenticationService {

    @Value("${google.drive.client.id}")
    private String momsClientId;

    @Value("${google.drive.client.secret}")
    private String momsClientSecret;

    @Autowired
    private NgrokConfig ngrokConfig;

    @Value("${google.oauth.token.dir}")
    private String tokenDir;

    private GoogleAuthorizationCodeFlow flow;

    private String getRedirectUri() {
        return ngrokConfig.getNgrokUrl() + "/oauth/callback";
    }

    @PostConstruct
    private void initFlow() {
        try {
            this.flow = new GoogleAuthorizationCodeFlow.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    GsonFactory.getDefaultInstance(),
                    momsClientId,
                    momsClientSecret,
                    Collections.singleton(FormsScopes.DRIVE))
                    .setDataStoreFactory(new FileDataStoreFactory(new File(tokenDir)))
                    .setAccessType("offline")
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize Google OAuth flow: " + e.getMessage(), e);
        }
    }

    @EventListener(ApplicationReadyEvent.class)
    public void logAuthStatus() {
        try {
            Credential credential = flow.loadCredential("mom");
            if (credential != null && credential.getRefreshToken() != null) {
                log.info("Google OAuth: Stored credential found. Ready to make API calls.");
            } else {
                String authorizationUrl = buildAuthorizationUrl();
                log.warn("============================================");
                log.warn("  GOOGLE AUTHORIZATION REQUIRED");
                log.warn("  Open this URL in your browser:");
                log.warn("  {}", authorizationUrl);
                log.warn("============================================");

                log.warn("\n\n\n URL to put on Google Cloud: {}", getRedirectUri());
            }
        } catch (Exception e) {
            log.error("Error checking Google OAuth credential status: {}", e.getMessage(), e);
        }
    }

    public Credential getStoredCredential() {
        try {
            Credential credential = flow.loadCredential("mom");
            if (credential != null && credential.getRefreshToken() != null) {
                return credential;
            }
            return null;
        } catch (Exception e) {
            log.error("Failed to load stored Google credential: {}", e.getMessage(), e);
            return null;
        }
    }

    public String buildAuthorizationUrl() {
        return flow.newAuthorizationUrl()
                .setRedirectUri(getRedirectUri())
                .set("prompt", "consent")
                .build();
    }

    public Credential exchangeCodeForCredential(String code) {
        try {
            TokenResponse tokenResponse = flow.newTokenRequest(code)
                    .setRedirectUri(getRedirectUri())
                    .execute();
            return flow.createAndStoreCredential(tokenResponse, "mom");
        } catch (Exception e) {
            log.error("Failed to exchange authorization code for credential: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to exchange authorization code: " + e.getMessage(), e);
        }
    }
}