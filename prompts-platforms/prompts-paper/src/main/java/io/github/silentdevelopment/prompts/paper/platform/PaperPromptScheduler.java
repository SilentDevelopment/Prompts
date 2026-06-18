package io.github.silentdevelopment.prompts.paper.platform;

import io.github.silentdevelopment.prompts.platform.PromptScheduler;
import io.github.silentdevelopment.prompts.platform.PromptTask;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.time.Duration;
import java.util.Objects;

public final class PaperPromptScheduler implements PromptScheduler {

    private final Plugin plugin;
    private final Server server;

    public PaperPromptScheduler(Plugin plugin) {
        this.plugin = Objects.requireNonNull(plugin, "plugin");
        this.server = plugin.getServer();
    }

    @Override
    public PromptTask schedule(Runnable task, Duration delay) {
        Objects.requireNonNull(task, "task");
        Objects.requireNonNull(delay, "delay");

        if (delay.isNegative()) {
            throw new IllegalArgumentException("delay cannot be negative.");
        }

        long ticks = Math.max(1L, (delay.toMillis() + 49L) / 50L);
        BukkitTask bukkitTask = server.getScheduler().runTaskLater(plugin, task, ticks);
        return new PaperPromptTask(bukkitTask);
    }

    @Override
    public void executeSync(Runnable task) {
        Objects.requireNonNull(task, "task");

        if (server.isPrimaryThread()) {
            task.run();
            return;
        }

        server.getScheduler().runTask(plugin, task);
    }

    @Override
    public void executeAsync(Runnable task) {
        Objects.requireNonNull(task, "task");

        server.getScheduler().runTaskAsynchronously(plugin, task);
    }

}