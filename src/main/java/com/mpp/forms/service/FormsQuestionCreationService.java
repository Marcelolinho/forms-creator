package com.mpp.forms.service;

import com.mpp.forms.ai.GeminiPromptHandler;
import com.mpp.forms.domain.forms.PromptParamsBo;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.stream.Stream;

@Service
public class FormsQuestionCreationService {

    private final GeminiPromptHandler geminiPromptHandler;

    public FormsQuestionCreationService(GeminiPromptHandler geminiPromptHandler) {
        this.geminiPromptHandler = geminiPromptHandler;
    }

    public String createQuestions(PromptParamsBo promptParams) {
        if (Stream.of(promptParams.getSubject(), promptParams.getSubject()).anyMatch(Objects::isNull)) {
            promptParams.setQuestionQuantity(10);
            promptParams.setSubject("Fonologia e Ortografia");
        }

        return geminiPromptHandler.createGoogleFormsWithQuestion(promptParams);
    }

    public String simplePrompt() {
        return geminiPromptHandler.simplePrompt();
    }
}
