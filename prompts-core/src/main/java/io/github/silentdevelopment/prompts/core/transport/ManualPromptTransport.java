package io.github.silentdevelopment.prompts.core.transport;

import io.github.silentdevelopment.prompts.core.input.DefaultPromptInput;
import io.github.silentdevelopment.prompts.Prompt;
import io.github.silentdevelopment.prompts.actor.PromptActorId;
import io.github.silentdevelopment.prompts.core.text.PlainPromptText;
import io.github.silentdevelopment.prompts.transport.PromptInputSink;
import io.github.silentdevelopment.prompts.session.PromptSession;
import io.github.silentdevelopment.prompts.transport.PromptTransport;

import java.util.Objects;

public final class ManualPromptTransport implements PromptTransport {

    private final String name;
    private PromptInputSink sink;

    public ManualPromptTransport(String name) {
        this.name = Objects.requireNonNull(name, "name");

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
        this.sink = Objects.requireNonNull(sink, "sink");
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
    }

    public boolean submit(PromptActorId actorId, String input) {
        Objects.requireNonNull(actorId, "actorId");
        Objects.requireNonNull(input, "input");

        if (sink == null) {
            return false;
        }

        return sink.accept(new DefaultPromptInput(actorId, PlainPromptText.of(input), name));
    }

}
