package io.github.silentdevelopment.prompts.core.parser;

import io.github.silentdevelopment.prompts.core.text.PlainPromptText;
import io.github.silentdevelopment.prompts.parser.ParseResult;
import io.github.silentdevelopment.prompts.text.PromptText;

import java.util.Objects;

public final class ParseResults {

    private ParseResults() {
    }

    public static <T> ParseResult<T> success(T value) {
        return ParseResult.success(value);
    }

    public static <T> ParseResult<T> failure(String message) {
        Objects.requireNonNull(message, "message");

        return ParseResult.failure(PlainPromptText.of(message));
    }

    public static <T> ParseResult<T> failure(PromptText message) {
        Objects.requireNonNull(message, "message");

        return ParseResult.failure(message);
    }

}