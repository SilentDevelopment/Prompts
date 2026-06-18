package io.github.silentdevelopment.prompts.result;

import io.github.silentdevelopment.prompts.text.PromptText;

import java.util.Objects;
import java.util.Optional;

public final class PromptResult<T> {

    private final PromptResultStatus status;
    private final T value;
    private final PromptText message;
    private final Throwable cause;

    private PromptResult(PromptResultStatus status, T value, PromptText message, Throwable cause) {
        this.status = Objects.requireNonNull(status, "status");
        this.value = value;
        this.message = message;
        this.cause = cause;
    }

    public static <T> PromptResult<T> success(T value) {
        return new PromptResult<>(PromptResultStatus.SUCCESS, value, null, null);
    }

    public static <T> PromptResult<T> cancelled() {
        return new PromptResult<>(PromptResultStatus.CANCELLED, null, null, null);
    }

    public static <T> PromptResult<T> timeout() {
        return new PromptResult<>(PromptResultStatus.TIMEOUT, null, null, null);
    }

    public static <T> PromptResult<T> actorUnavailable() {
        return new PromptResult<>(PromptResultStatus.ACTOR_UNAVAILABLE, null, null, null);
    }

    public static <T> PromptResult<T> transportUnavailable() {
        return new PromptResult<>(PromptResultStatus.TRANSPORT_UNAVAILABLE, null, null, null);
    }

    public static <T> PromptResult<T> shutdown() {
        return new PromptResult<>(PromptResultStatus.SHUTDOWN, null, null, null);
    }

    public static <T> PromptResult<T> failed(PromptText message) {
        return failed(message, null);
    }

    public static <T> PromptResult<T> failed(Throwable cause) {
        return new PromptResult<>(PromptResultStatus.FAILED, null, null, Objects.requireNonNull(cause, "cause"));
    }

    public static <T> PromptResult<T> failed(PromptText message, Throwable cause) {
        Objects.requireNonNull(message, "message");

        return new PromptResult<>(PromptResultStatus.FAILED, null, message, cause);
    }

    public static <T> PromptResult<T> alreadyActive() {
        return new PromptResult<>(PromptResultStatus.ALREADY_ACTIVE, null, null, null);
    }

    public PromptResultStatus status() {
        return status;
    }

    public boolean successful() {
        return status == PromptResultStatus.SUCCESS;
    }

    public Optional<T> value() {
        return Optional.ofNullable(value);
    }

    public Optional<PromptText> message() {
        return Optional.ofNullable(message);
    }

    public Optional<Throwable> cause() {
        return Optional.ofNullable(cause);
    }

}