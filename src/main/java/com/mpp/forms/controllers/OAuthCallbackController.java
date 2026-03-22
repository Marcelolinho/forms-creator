package com.mpp.forms.controllers;

import com.mpp.forms.service.GoogleAuthenticationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/oauth")
@Slf4j
public class OAuthCallbackController {

    private final GoogleAuthenticationService googleAuthenticationService;

    public OAuthCallbackController(GoogleAuthenticationService googleAuthenticationService) {
        this.googleAuthenticationService = googleAuthenticationService;
    }

    @GetMapping("/callback")
    public ResponseEntity<String> handleOAuthCallback(
            @RequestParam(value = "code", required = false) String code,
            @RequestParam(value = "error", required = false) String error) {

        if (error != null) {
            log.warn("Google OAuth authorization denied: {}", error);
            return ResponseEntity.badRequest()
                    .body("Authorization denied. Please restart the app and try again.");
        }

        if (code == null) {
            log.warn("Google OAuth callback received with no code and no error.");
            return ResponseEntity.badRequest()
                    .body("Invalid callback: no authorization code received.");
        }

        try {
            googleAuthenticationService.exchangeCodeForCredential(code);
            log.info("Google OAuth authorization completed successfully.");
            return ResponseEntity.ok("Authorization successful. You can close this tab.");
        } catch (Exception e) {
            log.error("Failed to exchange OAuth code: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body("Authorization failed: " + e.getMessage());
        }
    }
}