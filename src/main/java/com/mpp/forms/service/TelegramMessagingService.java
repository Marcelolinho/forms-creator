package com.mpp.forms.service;

import com.mpp.forms.controllers.dto.TelegramWebhookDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

@Service
public class TelegramMessagingService {

    private final String baseUrl = "https://api.telegram.org/bot";
    @Value("${telegram.bot.key}")
    private String telegramToken;
    @Value("${server.address}")
    private String serverPublicUrl;

    private final RestTemplate restTemplate;
    private final ApplicationContext applicationContext;
    private static final Logger log = LoggerFactory.getLogger(TelegramMessagingService.class);

    public TelegramMessagingService(RestTemplate restTemplate, ApplicationContext applicationContext) {
        this.restTemplate = restTemplate;
        this.applicationContext = applicationContext;
    }

    public void handleWebhookMessage(TelegramWebhookDto webhookDto) {

    }

    @EventListener(ApplicationReadyEvent.class)
    private void configureDefaultWebhook() {
        try {
            URI webhookConfigurationUri = new URI(String.format("%s%s/setWebhook", baseUrl, telegramToken));
            String webhookControllerUrl = String.format("https://%s/api/telegram/webhook", serverPublicUrl);

            Map<String, Object> mapOfRequestParams = new HashMap<>();
            mapOfRequestParams.put("url", webhookControllerUrl);

            RequestEntity setWebhookRequestEntity = new RequestEntity(mapOfRequestParams, HttpMethod.POST, webhookConfigurationUri);

            ResponseEntity<ResponseEntity> setWebhookResponseEntity = restTemplate.exchange(setWebhookRequestEntity, ResponseEntity.class);
            // TODO: Analisar o que ele responde aqui
            log.info("Webhook set for telegram on {}", webhookControllerUrl);
        } catch (URISyntaxException e) {
            log.error("Error at creating URI to configure default Webhook endpoint!");
            throw new RuntimeException(e.getMessage());
        }
    }

    @EventListener(ContextClosedEvent.class)
    private void deleteDefaultWebhook() {

    }

    private String generateSecretTelegram() {
        // TODO: Generate Dynamic code to use as the "X-Telegram-Bot-Api-Secret-Token" for the bot -> Docs: https://core.telegram.org/bots/api#setwebhook
        return null;
    }
}
