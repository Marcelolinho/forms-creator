package com.mpp.forms.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class FormsBo {

    private String formId;
    private Map<String, Map<String, Object>> formSettings;
    private List<QuestionBo> formQuestions;
}
