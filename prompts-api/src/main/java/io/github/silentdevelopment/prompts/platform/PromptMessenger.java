package io.github.silentdevelopment.prompts.platform;

import io.github.silentdevelopment.prompts.text.PromptText;
import io.github.silentdevelopment.prompts.actor.PromptActorId;

public interface PromptMessenger {

    void send(PromptActorId actorId, PromptText message);

}