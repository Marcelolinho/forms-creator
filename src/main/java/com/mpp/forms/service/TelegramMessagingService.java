package com.mpp.forms.service;

import com.mpp.forms.configuration.NgrokConfig;
import com.mpp.forms.controllers.dto.TelegramMessageDto;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
public class TelegramMessagingService {

    private final String baseUrl = "https://api.telegram.org/bot";
    private final NgrokConfig ngrokConfig;
    @Value("${telegram.bot.key}")
    private String telegramToken;

    private final RestTemplate restTemplate;
    private final ApplicationContext applicationContext;
    private static final Logger log = LoggerFactory.getLogger(TelegramMessagingService.class);

    private ArrayList<TelegramWebhookDto> webhookBatchToProcess = new ArrayList<>();

    public TelegramMessagingService(RestTemplate restTemplate, ApplicationContext applicationContext, NgrokConfig ngrokConfig) {
        this.restTemplate = restTemplate;
        this.applicationContext = applicationContext;
        this.ngrokConfig = ngrokConfig;
    }

    public void handleWebhookMessage(TelegramWebhookDto webhookDto) {
        // TODO: Save on a BATCH
        TelegramMessageDto messageDto = webhookDto.message();

        if (messageDto.text().equals("/start")) {

        }

        webhookBatchToProcess.add(webhookDto);
    }

    @EventListener(ApplicationReadyEvent.class)
    private void configureDefaultWebhook() {
        try {
            URI webhookConfigurationUri = new URI(String.format("%s%s/setWebhook", baseUrl, telegramToken));

            Map<String, Object> mapOfRequestParams = new HashMap<>();
            mapOfRequestParams.put("url", getDynamicWebhookUrl());

            RequestEntity setWebhookRequestEntity = new RequestEntity(mapOfRequestParams, HttpMethod.POST, webhookConfigurationUri);

            ResponseEntity<Map> telegramWebhookResponseEntity = restTemplate.exchange(setWebhookRequestEntity, Map.class);
            Map<String, Object> mapOfTelegramWebhookResponse = (Map<String, Object>) telegramWebhookResponseEntity.getBody();
            log.info("Webhook set for telegram on {}", getDynamicWebhookUrl());

        } catch (URISyntaxException e) {
            log.error("Error at creating URI to configure default Webhook endpoint!");
            throw new RuntimeException(e.getMessage());
        }
    }

    @EventListener(ContextClosedEvent.class)
    private void deleteDefaultWebhook() {
        try {
            URI webhookUnsetUrlUri = new URI(String.format("%s%s/deleteWebhook", baseUrl, telegramToken));

            Map<String, Object> mapOfRequestParams = new HashMap<>();
            mapOfRequestParams.put("url", getDynamicWebhookUrl());

            RequestEntity setWebhookRequestEntity = new RequestEntity(mapOfRequestParams, HttpMethod.POST, webhookUnsetUrlUri);

            ResponseEntity<Map> telegramUnsetWebhookResponseEntity = restTemplate.exchange(setWebhookRequestEntity, Map.class);
            Map<String, Object> mapOfTelegramWebhookResponse = (Map<String, Object>) telegramUnsetWebhookResponseEntity.getBody();
            log.info("Webhook unset for telegram on {}", getDynamicWebhookUrl());
        } catch (URISyntaxException e) {

        }
    }

    private String generateSecretTelegram() {
        // TODO: Generate Dynamic code to use as the "X-Telegram-Bot-Api-Secret-Token" for the bot -> Docs: https://core.telegram.org/bots/api#setwebhook
        return null;
    }

    private String getDynamicWebhookUrl() {
        return String.format("%s/api/telegram/webhook", ngrokConfig.getNgrokUrl());
    }
}
