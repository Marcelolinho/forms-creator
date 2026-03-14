package com.mpp.forms.controllers;

import com.mpp.forms.controllers.dto.TelegramWebhookDto;
import com.mpp.forms.service.TelegramMessagingService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/telegram/webhook")
public class TelegramWebhookController {

    private final TelegramMessagingService telegramMessagingService;

    public TelegramWebhookController(TelegramMessagingService telegramMessagingService) {
        this.telegramMessagingService = telegramMessagingService;
    }

    @PostMapping
    public void postWebhook(@RequestBody TelegramWebhookDto webhookDto) {
        telegramMessagingService.handleWebhookMessage(webhookDto);
    }
}
