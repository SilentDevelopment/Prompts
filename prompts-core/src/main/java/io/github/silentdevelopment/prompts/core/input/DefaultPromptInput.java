package io.github.silentdevelopment.prompts.core.input;

import io.github.silentdevelopment.prompts.actor.PromptActorId;
import io.github.silentdevelopment.prompts.text.PromptText;
import io.github.silentdevelopment.prompts.input.PromptInput;

import java.util.Objects;
import java.util.Optional;

public final class DefaultPromptInput implements PromptInput {

    private final PromptActorId actorId;
    private final PromptText text;
    private final String transportName;
    private final Object nativeEvent;

    public DefaultPromptInput(PromptActorId actorId, PromptText text, String transportName) {
        this(actorId, text, transportName, null);
    }

    public DefaultPromptInput(PromptActorId actorId, PromptText text, String transportName, Object nativeEvent) {
        this.actorId = Objects.requireNonNull(actorId, "actorId");
        this.text = Objects.requireNonNull(text, "text");
        this.transportName = Objects.requireNonNull(transportName, "transportName");
        this.nativeEvent = nativeEvent;

        if (transportName.isBlank()) {
            throw new IllegalArgumentException("transportName cannot be blank.");
        }
    }

    @Override
    public PromptActorId actorId() {
        return actorId;
    }

    @Override
    public PromptText text() {
        return text;
    }

    @Override
    public String transportName() {
        return transportName;
    }

    @Override
    public Optional<Object> nativeEvent() {
        return Optional.ofNullable(nativeEvent);
    }

}