package io.github.silentdevelopment.prompts.paper.platform;

import io.github.silentdevelopment.prompts.actor.PromptActorId;
import io.github.silentdevelopment.prompts.paper.text.PaperPromptText;
import io.github.silentdevelopment.prompts.paper.actor.PaperPromptActorIds;
import io.github.silentdevelopment.prompts.platform.PromptMessenger;
import io.github.silentdevelopment.prompts.platform.PromptScheduler;
import io.github.silentdevelopment.prompts.text.PromptText;
import net.kyori.adventure.text.Component;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public final class PaperPromptMessenger implements PromptMessenger {

    private final Server server;
    private final PromptScheduler scheduler;

    public PaperPromptMessenger(Server server, PromptScheduler scheduler) {
        this.server = Objects.requireNonNull(server, "server");
        this.scheduler = Objects.requireNonNull(scheduler, "scheduler");
    }

    @Override
    public void send(PromptActorId actorId, PromptText message) {
        Objects.requireNonNull(actorId, "actorId");
        Objects.requireNonNull(message, "message");

        scheduler.executeSync(() -> {
            Optional<UUID> uniqueId = PaperPromptActorIds.uuid(actorId);

            if (uniqueId.isEmpty()) {
                return;
            }

            Player player = server.getPlayer(uniqueId.get());

            if (player == null) {
                return;
            }

            player.sendMessage(component(message));
        });
    }

    private Component component(PromptText message) {
        if (message instanceof PaperPromptText paperText) {
            return paperText.component();
        }

        return Component.text(message.plainText());
    }

}