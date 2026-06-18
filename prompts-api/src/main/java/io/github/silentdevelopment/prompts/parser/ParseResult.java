package io.github.silentdevelopment.prompts.parser;

import io.github.silentdevelopment.prompts.text.PromptText;

import java.util.Objects;
import java.util.Optional;

public final class ParseResult<T> {

    private final boolean successful;
    private final T value;
    private final PromptText errorMessage;

    private ParseResult(boolean successful, T value, PromptText errorMessage) {
        this.successful = successful;
        this.value = value;
        this.errorMessage = errorMessage;
    }

    public static <T> ParseResult<T> success(T value) {
        return new ParseResult<>(true, value, null);
    }

    public static <T> ParseResult<T> failure(PromptText errorMessage) {
        Objects.requireNonNull(errorMessage, "errorMessage");

        return new ParseResult<>(false, null, errorMessage);
    }

    public boolean successful() {
        return successful;
    }

    public Optional<T> value() {
        return Optional.ofNullable(value);
    }

    public Optional<PromptText> errorMessage() {
        return Optional.ofNullable(errorMessage);
    }

}