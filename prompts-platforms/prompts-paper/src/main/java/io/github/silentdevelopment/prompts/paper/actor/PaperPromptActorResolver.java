package io.github.silentdevelopment.prompts.paper.actor;

import io.github.silentdevelopment.prompts.actor.PromptActor;
import io.github.silentdevelopment.prompts.actor.PromptActorId;
import io.github.silentdevelopment.prompts.platform.PromptActorResolver;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public final class PaperPromptActorResolver implements PromptActorResolver {

    private final Server server;

    public PaperPromptActorResolver(Server server) {
        this.server = Objects.requireNonNull(server, "server");
    }

    @Override
    public Optional<PromptActor> resolve(PromptActorId actorId) {
        Objects.requireNonNull(actorId, "actorId");

        Optional<UUID> uniqueId = PaperPromptActorIds.uuid(actorId);

        if (uniqueId.isEmpty()) {
            return Optional.empty();
        }

        Player player = server.getPlayer(uniqueId.get());

        if (player == null) {
            return Optional.empty();
        }

        return Optional.of(new PaperPromptActor(player));
    }

}