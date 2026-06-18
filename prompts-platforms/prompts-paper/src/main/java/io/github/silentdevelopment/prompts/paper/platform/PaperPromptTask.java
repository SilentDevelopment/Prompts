package io.github.silentdevelopment.prompts.paper.platform;

import io.github.silentdevelopment.prompts.platform.PromptTask;
import org.bukkit.scheduler.BukkitTask;

import java.util.Objects;

public final class PaperPromptTask implements PromptTask {

    private final BukkitTask task;

    public PaperPromptTask(BukkitTask task) {
        this.task = Objects.requireNonNull(task, "task");
    }

    @Override
    public void cancel() {
        task.cancel();
    }

    @Override
    public boolean cancelled() {
        return task.isCancelled();
    }

}