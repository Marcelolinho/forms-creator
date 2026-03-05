package com.mpp.forms.controllers.dto;

import java.time.OffsetTime;
import java.util.Map;

public record TelegramMessageDto(String messageId, TelegramFromDto from, OffsetTime date, String text, Map<String, Object> chat) {
}
