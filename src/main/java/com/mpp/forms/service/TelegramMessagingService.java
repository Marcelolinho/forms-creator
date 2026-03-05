package com.mpp.forms.service;

import com.mpp.forms.controllers.dto.TelegramWebhookDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Service
public class TelegramMessagingService {

    private final String baseUrl = "https://api.telegram.org/bot";
    private final RestTemplate restTemplate;
    @Value("${telegram.bot.key}")
    private String telegramToken;

    public TelegramMessagingService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private void handleWebhookMessage(TelegramWebhookDto webhookDto) {

    }

    @EventListener(ApplicationReadyEvent.class)
    private void configureWebhook() {
        String webhookConfigurationUrl = new URI(baseUrl + telegramToken + "");
    }
}
