package io.github.silentdevelopment.prompts.core.result;

import io.github.silentdevelopment.prompts.core.text.PlainPromptText;
import io.github.silentdevelopment.prompts.result.PromptResult;
import io.github.silentdevelopment.prompts.text.PromptText;

import java.util.Objects;

public final class PromptResults {

    private PromptResults() {
    }

    public static <T> PromptResult<T> failed(String message) {
        Objects.requireNonNull(message, "message");

        return PromptResult.failed(PlainPromptText.of(message));
    }

    public static <T> PromptResult<T> failed(String message, Throwable cause) {
        Objects.requireNonNull(message, "message");

        return PromptResult.failed(PlainPromptText.of(message), cause);
    }

    public static <T> PromptResult<T> failed(PromptText message) {
        Objects.requireNonNull(message, "message");

        return PromptResult.failed(message);
    }

    public static <T> PromptResult<T> failed(PromptText message, Throwable cause) {
        Objects.requireNonNull(message, "message");

        return PromptResult.failed(message, cause);
    }

}