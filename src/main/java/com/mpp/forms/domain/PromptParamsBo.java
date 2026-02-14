package com.mpp.forms.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PromptParamsBo {

    private String subject;
    private String schoolContent;
    private Integer questionQuantity;
}
