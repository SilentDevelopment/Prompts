package io.github.silentdevelopment.prompts.core.platform;

import io.github.silentdevelopment.prompts.platform.PromptScheduler;
import io.github.silentdevelopment.prompts.platform.PromptTask;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class ExecutorPromptScheduler implements PromptScheduler {

    private final ScheduledExecutorService scheduledExecutor;
    private final Executor syncExecutor;
    private final Executor asyncExecutor;

    public ExecutorPromptScheduler(ScheduledExecutorService scheduledExecutor, Executor syncExecutor, Executor asyncExecutor) {
        this.scheduledExecutor = Objects.requireNonNull(scheduledExecutor, "scheduledExecutor");
        this.syncExecutor = Objects.requireNonNull(syncExecutor, "syncExecutor");
        this.asyncExecutor = Objects.requireNonNull(asyncExecutor, "asyncExecutor");
    }

    @Override
    public PromptTask schedule(Runnable task, Duration delay) {
        Objects.requireNonNull(task, "task");
        Objects.requireNonNull(delay, "delay");

        if (delay.isNegative()) {
            throw new IllegalArgumentException("delay cannot be negative.");
        }

        return new ExecutorPromptTask(scheduledExecutor.schedule(task, delay.toMillis(), TimeUnit.MILLISECONDS));
    }

    @Override
    public void executeSync(Runnable task) {
        syncExecutor.execute(Objects.requireNonNull(task, "task"));
    }

    @Override
    public void executeAsync(Runnable task) {
        asyncExecutor.execute(Objects.requireNonNull(task, "task"));
    }

}