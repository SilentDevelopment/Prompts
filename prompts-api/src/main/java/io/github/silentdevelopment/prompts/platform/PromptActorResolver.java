package io.github.silentdevelopment.prompts.platform;

import io.github.silentdevelopment.prompts.actor.PromptActor;
import io.github.silentdevelopment.prompts.actor.PromptActorId;

import java.util.Optional;

public interface PromptActorResolver {

    Optional<PromptActor> resolve(PromptActorId actorId);

}