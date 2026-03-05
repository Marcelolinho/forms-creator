package com.mpp.forms.domain.telegram;

import lombok.Data;

import java.time.OffsetTime;
import java.util.Map;

@Data
public class TelegramMessageBo {
    private String messageId;
    private TelegramFromBo from;
    private OffsetTime date;
    private String text;
    private Map<String, Object> chat;
}
