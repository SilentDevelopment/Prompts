package io.github.silentdevelopment.prompts.input;

import io.github.silentdevelopment.prompts.actor.PromptActorId;
import io.github.silentdevelopment.prompts.text.PromptText;

import java.util.Optional;

public interface PromptInput {

    PromptActorId actorId();

    PromptText text();

    String transportName();

    Optional<Object> nativeEvent();

    default String plainText() {
        return text().plainText();
    }

}