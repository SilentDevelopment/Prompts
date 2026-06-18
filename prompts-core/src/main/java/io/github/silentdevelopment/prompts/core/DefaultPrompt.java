package io.github.silentdevelopment.prompts.core;

import io.github.silentdevelopment.prompts.Prompt;
import io.github.silentdevelopment.prompts.parser.PromptParser;
import io.github.silentdevelopment.prompts.text.PromptText;

import java.time.Duration;
import java.util.Objects;
import java.util.Optional;

public final class DefaultPrompt<T> implements Prompt<T> {

    private final PromptText message;
    private final PromptParser<T> parser;
    private final Duration timeout;
    private final String transportName;

    public DefaultPrompt(PromptText message, PromptParser<T> parser) {
        this(message, parser, null, null);
    }

    public DefaultPrompt(PromptText message, PromptParser<T> parser, Duration timeout, String transportName) {
        this.message = Objects.requireNonNull(message, "message");
        this.parser = Objects.requireNonNull(parser, "parser");
        this.timeout = timeout;
        this.transportName = transportName;
    }

    @Override
    public PromptText message() {
        return message;
    }

    @Override
    public PromptParser<T> parser() {
        return parser;
    }

    @Override
    public Optional<Duration> timeout() {
        return Optional.ofNullable(timeout);
    }

    @Override
    public Optional<String> transportName() {
        return Optional.ofNullable(transportName);
    }

}