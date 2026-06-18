package io.github.silentdevelopment.prompts.session;

import io.github.silentdevelopment.prompts.actor.PromptActorId;
import io.github.silentdevelopment.prompts.Prompt;
import io.github.silentdevelopment.prompts.result.PromptResult;

import java.time.Instant;
import java.util.concurrent.CompletionStage;

public interface PromptSession<T> {

    PromptActorId actorId();

    Prompt<T> prompt();

    String transportName();

    Instant createdAt();

    CompletionStage<PromptResult<T>> result();

}