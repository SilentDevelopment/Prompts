package io.github.silentdevelopment.prompts.core.actor;

import io.github.silentdevelopment.prompts.actor.PromptActor;
import io.github.silentdevelopment.prompts.actor.PromptActorId;

import java.util.Objects;

public record DefaultPromptActor(PromptActorId identifier) implements PromptActor {

    public DefaultPromptActor {
        Objects.requireNonNull(identifier, "id");
    }

    @Override
    public PromptActorId identifier() {
        return this.identifier;
    }

}