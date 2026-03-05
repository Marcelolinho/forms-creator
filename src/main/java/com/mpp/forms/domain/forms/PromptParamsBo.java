package com.mpp.forms.domain.forms;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PromptParamsBo {

    private String subject;
    private String schoolContent;
    private Integer questionQuantity;
    private String formsTitle;
}
