package io.github.silentdevelopment.prompts.core.session;

import io.github.silentdevelopment.prompts.Prompt;
import io.github.silentdevelopment.prompts.actor.PromptActorId;
import io.github.silentdevelopment.prompts.session.PromptSession;
import io.github.silentdevelopment.prompts.platform.PromptTask;
import io.github.silentdevelopment.prompts.result.PromptResult;

import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public final class DefaultPromptSession<T> implements PromptSession<T> {

    private final PromptActorId actorId;
    private final Prompt<T> prompt;
    private final String transportName;
    private final Instant createdAt;
    private final CompletableFuture<PromptResult<T>> result;
    private PromptTask timeoutTask;

    public DefaultPromptSession(PromptActorId actorId, Prompt<T> prompt, String transportName) {
        this.actorId = Objects.requireNonNull(actorId, "actorId");
        this.prompt = Objects.requireNonNull(prompt, "prompt");
        this.transportName = Objects.requireNonNull(transportName, "transportName");
        this.createdAt = Instant.now();
        this.result = new CompletableFuture<>();

        if (transportName.isBlank()) {
            throw new IllegalArgumentException("transportName cannot be blank.");
        }
    }

    @Override
    public PromptActorId actorId() {
        return actorId;
    }

    @Override
    public Prompt<T> prompt() {
        return prompt;
    }

    @Override
    public String transportName() {
        return transportName;
    }

    @Override
    public Instant createdAt() {
        return createdAt;
    }

    @Override
    public CompletionStage<PromptResult<T>> result() {
        return result;
    }

    public void timeoutTask(PromptTask timeoutTask) {
        this.timeoutTask = timeoutTask;
    }

    public boolean complete(PromptResult<T> result) {
        Objects.requireNonNull(result, "result");

        boolean completed = this.result.complete(result);

        if (!completed) {
            return false;
        }

        cancelTimeoutTask();
        return true;
    }

    public boolean completeExceptionally(Throwable throwable) {
        Objects.requireNonNull(throwable, "throwable");

        boolean completed = result.completeExceptionally(throwable);

        if (!completed) {
            return false;
        }

        cancelTimeoutTask();
        return true;
    }

    public void cancelTimeoutTask() {
        if (timeoutTask == null) {
            return;
        }

        timeoutTask.cancel();
        timeoutTask = null;
    }

}