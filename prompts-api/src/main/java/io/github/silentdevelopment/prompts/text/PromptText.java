package io.github.silentdevelopment.prompts.text;

public interface PromptText {

    String plainText();

    default boolean empty() {
        return plainText().isEmpty();
    }

    default boolean blank() {
        return plainText().isBlank();
    }

}