package com.mpp.forms.domain.telegram;

import lombok.Data;

@Data
public class TelegramFromBo {
    private Long id;
    private Boolean isBot;
    private String firstName;
    private String lastName;
}
