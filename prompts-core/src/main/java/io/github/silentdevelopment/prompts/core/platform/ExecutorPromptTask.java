package io.github.silentdevelopment.prompts.core.platform;

import io.github.silentdevelopment.prompts.platform.PromptTask;

import java.util.Objects;
import java.util.concurrent.ScheduledFuture;

public final class ExecutorPromptTask implements PromptTask {

    private final ScheduledFuture<?> future;

    public ExecutorPromptTask(ScheduledFuture<?> future) {
        this.future = Objects.requireNonNull(future, "future");
    }

    @Override
    public void cancel() {
        future.cancel(false);
    }

    @Override
    public boolean cancelled() {
        return future.isCancelled();
    }

}