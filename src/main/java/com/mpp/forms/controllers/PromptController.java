package com.mpp.forms.controllers;

import com.mpp.forms.domain.forms.WorkflowMessage;
import com.mpp.forms.service.FormsCreationService;
import com.mpp.forms.service.FormsQuestionCreationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/prompt")
public class PromptController {

    private final FormsCreationService formsCreationService;
    private final FormsQuestionCreationService formsQuestionCreationService;

    public PromptController(FormsCreationService formsCreationService, FormsQuestionCreationService formsQuestionCreationService) {
        this.formsCreationService = formsCreationService;
        this.formsQuestionCreationService = formsQuestionCreationService;
    }

    @PostMapping
    public String createFormsWithQuestions(WorkflowMessage workflowMessage) {
        return formsCreationService.createForms();
    }

    @PostMapping("/test")
    public String createQuestions(@RequestBody WorkflowMessage workflowMessage) {
        return formsQuestionCreationService.createQuestions(workflowMessage);
    }

    @PostMapping("/simple-prompt")
    public String createSimplePrompt() {
        return formsQuestionCreationService.simplePrompt();
    }

}
