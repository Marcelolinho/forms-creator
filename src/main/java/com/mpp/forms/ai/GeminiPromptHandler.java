package com.mpp.forms.ai;

import com.mpp.forms.domain.PromptParamsBo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;


@Repository
@Slf4j
public class GeminiPromptHandler {

    private final ChatClient client;

    @Value("classpath:prompts/google_forms_prompt.txt")
    private Resource systemPrompt;

    public GeminiPromptHandler(ChatClient.Builder chatClientBuilder) {
        this.client = chatClientBuilder.build();
    }

    public String createGoogleFormsWithQuestion(PromptParamsBo promptParams) {
        String subject = promptParams.getSubject();
        Integer questionQuantity = promptParams.getQuestionQuantity();
        String systemPromptWithParameters;

        try {
            String systemPromptString = systemPrompt.getContentAsString(StandardCharsets.UTF_8);
            systemPromptWithParameters = systemPromptString
                    .replace("{TEMA}", subject)
                    .replace("{QUANTIDADE}", String.valueOf(questionQuantity));
        } catch (IOException e) {
            log.error("Erro ao buscar o arquivo do prompt e transformá-lo em uma STRING: {}", e.getMessage());
            throw new RuntimeException("Erro ao buscar resource: " + e.getMessage());
        }

        String response = client.prompt()
                .user(systemPromptWithParameters)
                .call()
                .content();

        return response;
    }

    public String simplePrompt() {
        String prompt = "Hello";

        return client.prompt()
                .user(prompt)
                .call()
                .content();
    }
}
