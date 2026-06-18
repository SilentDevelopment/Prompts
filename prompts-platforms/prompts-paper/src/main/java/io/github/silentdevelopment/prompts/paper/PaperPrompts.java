package io.github.silentdevelopment.prompts.paper;

import io.github.silentdevelopment.prompts.Prompt;
import io.github.silentdevelopment.prompts.PromptService;
import io.github.silentdevelopment.prompts.actor.PromptActor;
import io.github.silentdevelopment.prompts.core.DefaultPromptService;
import io.github.silentdevelopment.prompts.paper.actor.PaperPromptActor;
import io.github.silentdevelopment.prompts.paper.actor.PaperPromptActorIds;
import io.github.silentdevelopment.prompts.paper.actor.PaperPromptActorResolver;
import io.github.silentdevelopment.prompts.paper.listener.PaperPromptLifecycleListener;
import io.github.silentdevelopment.prompts.paper.platform.PaperPromptMessenger;
import io.github.silentdevelopment.prompts.paper.platform.PaperPromptScheduler;
import io.github.silentdevelopment.prompts.platform.PromptScheduler;
import io.github.silentdevelopment.prompts.result.PromptResult;
import io.github.silentdevelopment.prompts.session.PromptSession;
import io.github.silentdevelopment.prompts.transport.PromptTransport;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;

public final class PaperPrompts {

    private final PromptService service;
    private final PaperPromptLifecycleListener lifecycleListener;
    private final PromptScheduler scheduler;

    private PaperPrompts(PromptService service, PromptScheduler scheduler, PaperPromptLifecycleListener lifecycleListener) {
        this.service = Objects.requireNonNull(service, "service");
        this.scheduler = Objects.requireNonNull(scheduler, "scheduler");
        this.lifecycleListener = lifecycleListener;
    }

    public static Builder builder(Plugin plugin) {
        return new Builder(plugin);
    }

    public PromptActor actor(Player player) {
        return new PaperPromptActor(player);
    }

    /**
     * Opens a prompt for the given player.
     *
     * <p>The returned stage is completed by whichever thread completes the underlying prompt.
     * For chat prompts, that may be Paper's asynchronous chat event thread. For timeout prompts,
     * that may be the server scheduler thread. Consumers should not assume the completion callback
     * runs on the primary server thread.</p>
     *
     * <p>If the callback touches Bukkit/Paper API, use {@link #askAndHandleSync(Player, Prompt, Consumer)}
     * or manually schedule back onto the server thread.</p>
     *
     * @param player the player to prompt
     * @param prompt the prompt to open
     * @param <T> the parsed prompt result type
     * @return a stage completed with the prompt result
     */
    public <T> CompletionStage<PromptResult<T>> ask(Player player, Prompt<T> prompt) {
        Objects.requireNonNull(player, "player");
        Objects.requireNonNull(prompt, "prompt");

        return service.ask(actor(player), prompt);
    }

    /**
     * Opens a prompt for the given player and invokes the handler on the primary server thread.
     *
     * <p>This is a convenience method for Paper consumers. It is especially useful for chat prompts,
     * because {@code AsyncChatEvent} may complete the prompt from an asynchronous thread.</p>
     *
     * <p>The returned stage is still the original prompt result stage. The supplied handler is scheduled
     * separately through the Paper prompt scheduler.</p>
     *
     * @param player the player to prompt
     * @param prompt the prompt to open
     * @param handler the result handler to run on the primary server thread
     * @param <T> the parsed prompt result type
     * @return a stage completed with the prompt result
     */
    public <T> CompletionStage<PromptResult<T>> askAndHandleSync(Player player, Prompt<T> prompt, Consumer<? super PromptResult<T>> handler) {
        Objects.requireNonNull(handler, "handler");

        CompletionStage<PromptResult<T>> stage = ask(player, prompt);
        stage.thenAccept(result -> scheduler.executeSync(() -> handler.accept(result)));
        return stage;
    }

    public boolean hasActivePrompt(Player player) {
        Objects.requireNonNull(player, "player");

        return service.hasActivePrompt(PaperPromptActorIds.of(player));
    }

    public Optional<PromptSession<?>> activePrompt(Player player) {
        Objects.requireNonNull(player, "player");

        return service.activePrompt(PaperPromptActorIds.of(player));
    }

    public void cancel(Player player) {
        Objects.requireNonNull(player, "player");

        service.cancel(PaperPromptActorIds.of(player));
    }

    public void shutdown() {
        if (lifecycleListener != null) {
            HandlerList.unregisterAll(lifecycleListener);
        }

        service.shutdown();
    }

    public PromptService service() {
        return service;
    }

    public static final class Builder {

        private final Plugin plugin;
        private final List<PromptTransport> transports;

        private Builder(Plugin plugin) {
            this.plugin = Objects.requireNonNull(plugin, "plugin");
            this.transports = new ArrayList<>();
        }

        public Builder transport(PromptTransport transport) {
            transports.add(Objects.requireNonNull(transport, "transport"));
            return this;
        }

        public PaperPrompts build() {
            PromptScheduler scheduler = new PaperPromptScheduler(plugin);

            PromptService service = DefaultPromptService.builder()
                    .scheduler(scheduler)
                    .actorResolver(new PaperPromptActorResolver(plugin.getServer()))
                    .messenger(new PaperPromptMessenger(plugin.getServer(), scheduler))
                    .transports(transports)
                    .build();

            PaperPromptLifecycleListener lifecycleListener = new PaperPromptLifecycleListener(service);
            plugin.getServer().getPluginManager().registerEvents(lifecycleListener, plugin);

            return new PaperPrompts(service, scheduler, lifecycleListener);
        }

    }

}