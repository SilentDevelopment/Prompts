package io.github.silentdevelopment.prompts;

import io.github.silentdevelopment.prompts.text.PromptText;
import io.github.silentdevelopment.prompts.parser.PromptParser;

import java.time.Duration;
import java.util.Optional;

public interface Prompt<T> {

    PromptText message();

    PromptParser<T> parser();

    Optional<Duration> timeout();

    Optional<String> transportName();

}