package com.mpp.forms.service;

import com.mpp.forms.configuration.NgrokConfig;
import com.mpp.forms.controllers.dto.TelegramMessageDto;
import com.mpp.forms.controllers.dto.TelegramWebhookDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.mpp.forms.domain.forms.WorkflowMessage;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class TelegramMessagingService {

    private final String baseUrl = "https://api.telegram.org/bot";
    private final NgrokConfig ngrokConfig;
    @Value("${telegram.bot.key}")
    private String telegramToken;
    @Value("${telegram.authorized.user.id}")
    private Long authorizedUserId;

    private final RestTemplate restTemplate;
    private final FormsQuestionCreationService formsQuestionCreationService;
    private final FormsCreationService formsCreationService;
    private static final Logger log = LoggerFactory.getLogger(TelegramMessagingService.class);

    public TelegramMessagingService(RestTemplate restTemplate,
                                    NgrokConfig ngrokConfig,
                                    FormsQuestionCreationService formsQuestionCreationService,
                                    FormsCreationService formsCreationService) {
        this.restTemplate = restTemplate;
        this.ngrokConfig = ngrokConfig;
        this.formsQuestionCreationService = formsQuestionCreationService;
        this.formsCreationService = formsCreationService;
    }

    public void handleWebhookMessage(TelegramWebhookDto webhookDto) {
        TelegramMessageDto messageDto = webhookDto.message();
        Object chatId = messageDto.chat().get("id");
        String text = messageDto.text();

//        if (!webhookDto.message().from().id().equals(authorizedUserId)) {
//            return;
//        }

        if (text == null) {
            return;
        }

        if ("/start".equals(text)) {
            sendTelegramReply(chatId, "Olá! Envie uma mensagem no formato: Conteudo: X Questoes: N");
            return;
        }

        Optional<WorkflowMessage> optionalWorkflow = WorkflowMessage.parse(text);

        if (optionalWorkflow.isEmpty()) {
            sendTelegramReply(chatId, "Formato inválido. Use: Conteudo: X Questoes: N");
            return;
        }

        WorkflowMessage workflowMessage = optionalWorkflow.get();
        try {
            String createdFormsId = formsCreationService.createForms();
            String aiJson = formsQuestionCreationService.createQuestions(workflowMessage);
            formsCreationService.populateForms(createdFormsId, aiJson);
            sendTelegramReply(chatId,
                    "Criei um formulário com " + workflowMessage.questionCount()
                            + " questões sobre " + workflowMessage.subject());
        } catch (Exception e) {
            log.error("Error processing workflow for chatId {}: {}", chatId, e.getMessage(), e);
            sendTelegramReply(chatId, "Desculpa, algo deu errado. Tente novamente em alguns minutos.");
        }
    }

    private void sendTelegramReply(Object chatId, String text) {
        try {
            URI sendMessageUri = new URI(String.format("%s%s/sendMessage", baseUrl, telegramToken));

            Map<String, Object> body = new HashMap<>();
            body.put("chat_id", chatId);
            body.put("text", text);

            restTemplate.exchange(RequestEntity.post(sendMessageUri).body(body), Map.class);
        } catch (Exception e) {
            log.error("Failed to send Telegram reply to chatId {}: {}", chatId, e.getMessage(), e);
        }
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

    private String getDynamicWebhookUrl() {
        return String.format("%s/api/telegram/webhook", ngrokConfig.getNgrokUrl());
    }

    private String generateSecretTelegram() {
        // TODO: Generate Dynamic code to use as the "X-Telegram-Bot-Api-Secret-Token" for the bot -> Docs: https://core.telegram.org/bots/api#setwebhook
        return null;
    }
}
