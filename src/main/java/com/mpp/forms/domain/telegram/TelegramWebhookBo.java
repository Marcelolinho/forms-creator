package com.mpp.forms.domain.telegram;

import lombok.Data;

@Data
public class TelegramWebhookBo {
    private Long updateId;
    private TelegramMessageBo message;
}
