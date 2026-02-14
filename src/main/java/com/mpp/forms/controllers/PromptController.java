package com.mpp.forms.controllers;

import com.mpp.forms.domain.PromptParamsBo;
import com.mpp.forms.service.FormsCreationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/prompt")
public class PromptController {

    private final FormsCreationService formsCreationService;

    public PromptController(FormsCreationService formsCreationService) {
        this.formsCreationService = formsCreationService;
    }

    @PostMapping
    public String createFormsWithQuestions(PromptParamsBo promptParams) {
        return formsCreationService.createFormsWithQuestions(promptParams);
    }

}
