package io.github.silentdevelopment.prompts.core;

import io.github.silentdevelopment.prompts.Prompt;
import io.github.silentdevelopment.prompts.core.text.PlainPromptText;
import io.github.silentdevelopment.prompts.parser.ParseResult;
import io.github.silentdevelopment.prompts.parser.PromptParser;
import io.github.silentdevelopment.prompts.text.PromptText;

import java.time.Duration;

public final class Prompts {

    private Prompts() {}

    public static Prompt<String> string(String message) {
        return string(PlainPromptText.of(message));
    }

    public static Prompt<String> string(PromptText message) {
        return new DefaultPrompt<>(message, ParseResult::success);
    }

    public static <T> Prompt<T> of(String message, PromptParser<T> parser) {
        return of(PlainPromptText.of(message), parser);
    }

    public static <T> Prompt<T> of(PromptText message, PromptParser<T> parser) {
        return new DefaultPrompt<>(message, parser);
    }

    public static <T> Prompt<T> of(String message, PromptParser<T> parser, Duration timeout, String transportName) {
        return new DefaultPrompt<>(PlainPromptText.of(message), parser, timeout, transportName);
    }

}