package io.github.silentdevelopment.prompts.core.platform;

import io.github.silentdevelopment.prompts.platform.PromptTask;

import java.util.concurrent.atomic.AtomicBoolean;

public final class NoOpPromptTask implements PromptTask {

    private final AtomicBoolean cancelled = new AtomicBoolean();

    @Override
    public void cancel() {
        cancelled.set(true);
    }

    @Override
    public boolean cancelled() {
        return cancelled.get();
    }

}