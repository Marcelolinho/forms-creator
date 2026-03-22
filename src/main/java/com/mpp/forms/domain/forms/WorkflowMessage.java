package com.mpp.forms.domain.forms;

import lombok.Getter;
import lombok.Setter;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
@Setter
public class WorkflowMessage {

    private static final Pattern MESSAGE_PATTERN =
        Pattern.compile("(?i)conteudo:\\s*(.+?)\\s+questoes:\\s*(\\d+)");

    private final String subject;
    private final int questionCount;

    private WorkflowMessage(String subject, int questionCount) {
        this.subject = subject;
        this.questionCount = questionCount;
    }

    public String subject() {
        return subject;
    }

    public int questionCount() {
        return questionCount;
    }

    public static Optional<WorkflowMessage> parse(String text) {
        if (text == null) {
            return Optional.empty();
        }

        Matcher matcher = MESSAGE_PATTERN.matcher(text.trim());

        if (!matcher.find()) {
            return Optional.empty();
        }

        String subject = matcher.group(1).trim();
        int questionCount = Integer.parseInt(matcher.group(2));
        return Optional.of(new WorkflowMessage(subject, questionCount));
    }
}