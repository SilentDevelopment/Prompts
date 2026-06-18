package io.github.silentdevelopment.prompts.paper.actor;

import io.github.silentdevelopment.prompts.actor.PromptActor;
import io.github.silentdevelopment.prompts.actor.PromptActorId;
import org.bukkit.entity.Player;

import java.util.Objects;

public final class PaperPromptActor implements PromptActor {

    private final PromptActorId identifier;

    public PaperPromptActor(Player player) {
        Objects.requireNonNull(player, "player");

        this.identifier = PaperPromptActorIds.of(player);
    }

    @Override
    public PromptActorId identifier() {
        return identifier;
    }

}