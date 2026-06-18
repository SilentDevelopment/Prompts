package io.github.silentdevelopment.prompts.paper.actor;

import io.github.silentdevelopment.prompts.actor.PromptActorId;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public final class PaperPromptActorIds {

    public static final String NAMESPACE = "paper";

    private PaperPromptActorIds() {
    }

    public static PromptActorId of(Player player) {
        Objects.requireNonNull(player, "player");

        return PromptActorId.of(NAMESPACE, player.getUniqueId().toString());
    }

    public static Optional<UUID> uuid(PromptActorId actorId) {
        Objects.requireNonNull(actorId, "actorId");

        if (!NAMESPACE.equals(actorId.namespace())) {
            return Optional.empty();
        }

        try {
            return Optional.of(UUID.fromString(actorId.value()));
        } catch (IllegalArgumentException exception) {
            return Optional.empty();
        }
    }

}