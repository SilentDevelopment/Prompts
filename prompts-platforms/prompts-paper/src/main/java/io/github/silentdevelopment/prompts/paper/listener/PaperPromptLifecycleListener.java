package io.github.silentdevelopment.prompts.paper.listener;

import io.github.silentdevelopment.prompts.PromptService;
import io.github.silentdevelopment.prompts.paper.actor.PaperPromptActorIds;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Objects;

public final class PaperPromptLifecycleListener implements Listener {

    private final PromptService service;

    public PaperPromptLifecycleListener(PromptService service) {
        this.service = Objects.requireNonNull(service, "service");
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        service.cancel(PaperPromptActorIds.of(event.getPlayer()));
    }

}