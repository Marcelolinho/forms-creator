package com.mpp.forms.service;

import com.google.api.client.auth.oauth2.Credential;
import com.mpp.forms.domain.forms.PromptParamsBo;
import org.springframework.stereotype.Service;

@Service
public class FormsCreationService {

    private final GoogleAuthenticationService googleAuthenticationService;
    private String token;

    public FormsCreationService(GoogleAuthenticationService googleAuthenticationService) {
        this.googleAuthenticationService = googleAuthenticationService;
    }

    public String createForms(PromptParamsBo promptParams) {
        Credential teste = googleAuthenticationService.getGoogleAppCredentials();

        if (teste == null) {
            return "deu errado";
        }
        return "deu certo";
    }
}
