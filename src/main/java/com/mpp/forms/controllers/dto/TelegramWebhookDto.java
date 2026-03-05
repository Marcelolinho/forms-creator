package com.mpp.forms.controllers.dto;

import com.mpp.forms.domain.telegram.TelegramMessageBo;

public record TelegramWebhookDto(Long updateId, TelegramMessageDto message) {
}
