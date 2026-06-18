package io.github.silentdevelopment.prompts;

import io.github.silentdevelopment.prompts.actor.PromptActor;
import io.github.silentdevelopment.prompts.actor.PromptActorId;
import io.github.silentdevelopment.prompts.result.PromptResult;
import io.github.silentdevelopment.prompts.session.PromptSession;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface PromptService {

    <T> CompletionStage<PromptResult<T>> ask(PromptActor actor, Prompt<T> prompt);

    boolean hasActivePrompt(PromptActorId actorId);

    Optional<PromptSession<?>> activePrompt(PromptActorId actorId);

    void cancel(PromptActorId actorId);

    void shutdown();

}