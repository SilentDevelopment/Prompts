package io.github.silentdevelopment.prompts.platform;

import java.time.Duration;

public interface PromptScheduler {

    PromptTask schedule(Runnable task, Duration delay);

    void executeSync(Runnable task);

    void executeAsync(Runnable task);

}