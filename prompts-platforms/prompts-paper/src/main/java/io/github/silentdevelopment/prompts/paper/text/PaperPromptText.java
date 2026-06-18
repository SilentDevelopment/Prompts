package io.github.silentdevelopment.prompts.paper.text;

import io.github.silentdevelopment.prompts.text.PromptText;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import java.util.Objects;

public final class PaperPromptText implements PromptText {

    private static final PlainTextComponentSerializer PLAIN = PlainTextComponentSerializer.plainText();

    private final Component component;
    private final String plainText;

    private PaperPromptText(Component component) {
        this.component = Objects.requireNonNull(component, "component");
        this.plainText = PLAIN.serialize(component);
    }

    public static PaperPromptText of(Component component) {
        return new PaperPromptText(component);
    }

    public Component component() {
        return component;
    }

    @Override
    public String plainText() {
        return plainText;
    }

    @Override
    public String toString() {
        return plainText;
    }

}