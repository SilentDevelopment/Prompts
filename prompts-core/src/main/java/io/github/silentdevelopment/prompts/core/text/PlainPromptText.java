package io.github.silentdevelopment.prompts.core.text;

import io.github.silentdevelopment.prompts.text.PromptText;

import java.util.Objects;

public record PlainPromptText(String plainText) implements PromptText {

    public static final PlainPromptText EMPTY = new PlainPromptText("");

    public PlainPromptText {
        Objects.requireNonNull(plainText, "plainText");
    }

    public static PlainPromptText of(String plainText) {
        return new PlainPromptText(plainText);
    }

}