package com.mpp.forms.controllers.dto;

import java.util.Date;
import java.util.Map;

public record TelegramMessageDto(String messageId, TelegramFromDto from, long date, String text, Map<String, Object> chat) {
}
