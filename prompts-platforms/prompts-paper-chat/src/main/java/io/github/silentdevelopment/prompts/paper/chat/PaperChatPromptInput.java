package io.github.silentdevelopment.prompts.paper.chat;

import io.github.silentdevelopment.prompts.actor.PromptActorId;
import io.github.silentdevelopment.prompts.input.PromptInput;
import io.github.silentdevelopment.prompts.text.PromptText;

import java.util.Objects;
import java.util.Optional;

public record PaperChatPromptInput(PromptActorId actorId, PromptText text, String transportName, Object event) implements PromptInput {

    public PaperChatPromptInput {
        Objects.requireNonNull(actorId, "actorId");
        Objects.requireNonNull(text, "text");
        Objects.requireNonNull(transportName, "transportName");

        if (transportName.isBlank()) {
            throw new IllegalArgumentException("transportName cannot be blank.");
        }
    }

    @Override
    public Optional<Object> nativeEvent() {
        return Optional.ofNullable(event);
    }

}