package com.mpp.forms.service;

import com.mpp.forms.ai.GeminiPromptHandler;
import com.mpp.forms.domain.forms.WorkflowMessage;
import org.springframework.stereotype.Service;

@Service
public class FormsQuestionCreationService {

    private final GeminiPromptHandler geminiPromptHandler;

    public FormsQuestionCreationService(GeminiPromptHandler geminiPromptHandler) {
        this.geminiPromptHandler = geminiPromptHandler;
    }

    public String createQuestions(WorkflowMessage workflowMessage) {
        return geminiPromptHandler.createGoogleFormsWithQuestion(workflowMessage);
    }

    public String simplePrompt() {
        return geminiPromptHandler.simplePrompt();
    }
}
