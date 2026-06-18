package io.github.silentdevelopment.prompts.actor;

import java.util.Objects;

public final class PromptActorId {

    private final String namespace;
    private final String value;

    private PromptActorId(String namespace, String value) {
        this.namespace = Objects.requireNonNull(namespace, "namespace");
        this.value = Objects.requireNonNull(value, "value");

        if (namespace.isBlank()) {
            throw new IllegalArgumentException("namespace cannot be blank.");
        }

        if (value.isBlank()) {
            throw new IllegalArgumentException("value cannot be blank.");
        }
    }

    public static PromptActorId of(String namespace, String value) {
        return new PromptActorId(namespace, value);
    }

    public String namespace() {
        return namespace;
    }

    public String value() {
        return value;
    }

    public String asString() {
        return namespace + ":" + value;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof PromptActorId other)) {
            return false;
        }

        return namespace.equals(other.namespace) && value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(namespace, value);
    }

    @Override
    public String toString() {
        return asString();
    }

}