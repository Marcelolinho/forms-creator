package com.mpp.forms.service;

import com.google.api.client.auth.oauth2.Credential;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FormsCreationService {

    private final GoogleAuthenticationService googleAuthenticationService;
    private final String googleApiBaseUrl = "https://forms.googleapis.com";
    private final RestTemplate restTemplate;

    public FormsCreationService(GoogleAuthenticationService googleAuthenticationService, RestTemplate restTemplate) {
        this.googleAuthenticationService = googleAuthenticationService;
        this.restTemplate = restTemplate;
    }

    private HttpHeaders buildAuthHeaders() {
        Credential credential = googleAuthenticationService.getStoredCredential();
        if (credential == null) {
            throw new RuntimeException(
                "Google account not yet authorized. Please open the authorization URL from the application logs and complete the OAuth flow.");
        }
        try {
            // Refresh the access token if it is expired
            if (credential.getExpiresInSeconds() != null && credential.getExpiresInSeconds() <= 60) {
                credential.refreshToken();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to refresh Google access token: " + e.getMessage(), e);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("Authorization", "Bearer " + credential.getAccessToken());
        return headers;
    }

    public String createForms() {
        HttpHeaders headers = buildAuthHeaders();

        Map<String, Map<String, String>> newFormsBody = new HashMap<>();
        newFormsBody.put("info", Map.of("title", "Novo Formulario Criado Pelo Marcelo", "documentTitle", "Novo Formulario Criado Pelo Marcelo"));

        Map<String, Object> blankFormsResponseEntity = (Map<String, Object>) restTemplate.exchange(
                RequestEntity.post(String.format("%s/forms", googleApiBaseUrl)).headers(headers).body(newFormsBody),
                Map.class).getBody();

        String formsId = blankFormsResponseEntity.get("formId").toString();

        if (formsId == null || formsId.isEmpty()) {
            throw new RuntimeException("Erro ao tentar criar o formulario, nao foi retornaro o ID dele corretamente.");
        }

        return formsId;
    }

    public void populateForms(String formsId, String formsJson) {
        HttpHeaders headers = buildAuthHeaders();

        Map<String, Object> blankFormsResponseEntity = (Map<String, Object>) restTemplate.exchange(
                RequestEntity.post(String.format("%s/forms/%s/batchUpdate", googleApiBaseUrl, formsId)).headers(headers).body(formsJson),
                Map.class).getBody();

        if (((List<Map<String, Object>>) blankFormsResponseEntity.get("replies")).isEmpty()) {
            throw new RuntimeException("Questoes nao foram criadas");
        }
    }
}