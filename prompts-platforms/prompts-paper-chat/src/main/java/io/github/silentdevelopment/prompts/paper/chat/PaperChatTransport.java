package io.github.silentdevelopment.prompts.paper.chat;

import io.github.silentdevelopment.prompts.Prompt;
import io.github.silentdevelopment.prompts.paper.actor.PaperPromptActorIds;
import io.github.silentdevelopment.prompts.paper.text.PaperPromptText;
import io.github.silentdevelopment.prompts.session.PromptSession;
import io.github.silentdevelopment.prompts.transport.PromptInputSink;
import io.github.silentdevelopment.prompts.transport.PromptTransport;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public final class PaperChatTransport implements PromptTransport, Listener {

    public static final String DEFAULT_NAME = "chat";

    private final Plugin plugin;
    private final String name;
    private final AtomicBoolean shutdown;
    private PromptInputSink sink;

    public PaperChatTransport(Plugin plugin) {
        this(plugin, DEFAULT_NAME);
    }

    public PaperChatTransport(Plugin plugin, String name) {
        this.plugin = Objects.requireNonNull(plugin, "plugin");
        this.name = Objects.requireNonNull(name, "name");
        this.shutdown = new AtomicBoolean();

        if (name.isBlank()) {
            throw new IllegalArgumentException("name cannot be blank.");
        }
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public boolean supports(Prompt<?> prompt) {
        Objects.requireNonNull(prompt, "prompt");

        return true;
    }

    @Override
    public void bind(PromptInputSink sink) {
        if (this.sink != null) {
            throw new IllegalStateException("Prompt transport is already bound.");
        }

        this.sink = Objects.requireNonNull(sink, "sink");
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void open(PromptSession<?> session) {
        Objects.requireNonNull(session, "session");
    }

    @Override
    public void close(PromptSession<?> session) {
        Objects.requireNonNull(session, "session");
    }

    @Override
    public void shutdown() {
        if (!shutdown.compareAndSet(false, true)) {
            return;
        }

        HandlerList.unregisterAll(this);
    }

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        if (shutdown.get()) {
            return;
        }

        if (event.isCancelled()) {
            return;
        }

        PromptInputSink currentSink = sink;

        if (currentSink == null) {
            return;
        }

        PaperChatPromptInput input = new PaperChatPromptInput(PaperPromptActorIds.of(event.getPlayer()), PaperPromptText.of(event.message()), name, event);
        boolean consumed = currentSink.accept(input);

        if (!consumed) {
            return;
        }

        event.setCancelled(true);
    }

}