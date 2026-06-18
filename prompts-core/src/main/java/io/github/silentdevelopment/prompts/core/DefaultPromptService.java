package io.github.silentdevelopment.prompts.core;

import io.github.silentdevelopment.prompts.Prompt;
import io.github.silentdevelopment.prompts.PromptService;
import io.github.silentdevelopment.prompts.actor.PromptActor;
import io.github.silentdevelopment.prompts.actor.PromptActorId;
import io.github.silentdevelopment.prompts.core.result.PromptResults;
import io.github.silentdevelopment.prompts.core.session.DefaultPromptSession;
import io.github.silentdevelopment.prompts.core.text.PlainPromptText;
import io.github.silentdevelopment.prompts.input.PromptInput;
import io.github.silentdevelopment.prompts.parser.ParseResult;
import io.github.silentdevelopment.prompts.platform.PromptActorResolver;
import io.github.silentdevelopment.prompts.platform.PromptMessenger;
import io.github.silentdevelopment.prompts.platform.PromptScheduler;
import io.github.silentdevelopment.prompts.platform.PromptTask;
import io.github.silentdevelopment.prompts.result.PromptResult;
import io.github.silentdevelopment.prompts.session.PromptSession;
import io.github.silentdevelopment.prompts.text.PromptText;
import io.github.silentdevelopment.prompts.transport.PromptInputSink;
import io.github.silentdevelopment.prompts.transport.PromptTransport;

import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public final class DefaultPromptService implements PromptService, PromptInputSink {

    private static final PromptText DEFAULT_INVALID_INPUT = PlainPromptText.of("Invalid input.");

    private final PromptScheduler scheduler;
    private final PromptMessenger messenger;
    private final PromptActorResolver actorResolver;
    private final Map<String, PromptTransport> transports;
    private final Map<PromptActorId, DefaultPromptSession<?>> sessions;
    private final AtomicBoolean shutdown;

    private DefaultPromptService(Builder builder) {
        this.scheduler = Objects.requireNonNull(builder.scheduler, "scheduler");
        this.messenger = Objects.requireNonNull(builder.messenger, "messenger");
        this.actorResolver = Objects.requireNonNull(builder.actorResolver, "actorResolver");
        this.transports = new LinkedHashMap<>();
        this.sessions = new ConcurrentHashMap<>();
        this.shutdown = new AtomicBoolean();

        if (builder.transports.isEmpty()) {
            throw new IllegalStateException("At least one prompt transport is required.");
        }

        for (PromptTransport transport : builder.transports) {
            registerTransport(transport);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public <T> CompletionStage<PromptResult<T>> ask(PromptActor actor, Prompt<T> prompt) {
        Objects.requireNonNull(actor, "actor");
        Objects.requireNonNull(prompt, "prompt");

        if (shutdown.get()) {
            return completed(PromptResult.shutdown());
        }

        PromptActorId actorId = Objects.requireNonNull(actor.identifier(), "actor.id()");

        if (actorResolver.resolve(actorId).isEmpty()) {
            return completed(PromptResult.actorUnavailable());
        }

        Optional<Duration> timeout = prompt.timeout();

        if (timeout.isPresent() && invalidTimeout(timeout.get())) {
            return completed(PromptResults.failed("Prompt timeout must be positive."));
        }

        Optional<PromptTransport> selectedTransport = selectTransport(prompt);

        if (selectedTransport.isEmpty()) {
            return completed(PromptResult.transportUnavailable());
        }

        PromptTransport transport = selectedTransport.get();
        DefaultPromptSession<T> session = new DefaultPromptSession<>(actorId, prompt, transport.name());
        DefaultPromptSession<?> previous = sessions.putIfAbsent(actorId, session);

        if (previous != null) {
            return completed(PromptResult.alreadyActive());
        }

        try {
            scheduleTimeout(session);
            transport.open(session);
            sendPromptMessage(session, transport);
            return session.result();
        } catch (Throwable throwable) {
            finish(session, PromptResults.failed(("Failed to open prompt."), throwable));
            return session.result();
        }
    }

    @Override
    public boolean hasActivePrompt(PromptActorId actorId) {
        Objects.requireNonNull(actorId, "actorId");

        return sessions.containsKey(actorId);
    }

    @Override
    public Optional<PromptSession<?>> activePrompt(PromptActorId actorId) {
        Objects.requireNonNull(actorId, "actorId");

        return Optional.ofNullable(sessions.get(actorId));
    }

    @Override
    public void cancel(PromptActorId actorId) {
        Objects.requireNonNull(actorId, "actorId");

        DefaultPromptSession<?> session = sessions.get(actorId);

        if (session == null) {
            return;
        }

        finish(session, PromptResult.cancelled());
    }

    @Override
    public void shutdown() {
        if (!shutdown.compareAndSet(false, true)) {
            return;
        }

        List<DefaultPromptSession<?>> activeSessions = new ArrayList<>(sessions.values());

        for (DefaultPromptSession<?> session : activeSessions) {
            finish(session, PromptResult.shutdown());
        }

        for (PromptTransport transport : transports.values()) {
            transport.shutdown();
        }
    }

    @Override
    public boolean accept(PromptInput input) {
        Objects.requireNonNull(input, "input");

        DefaultPromptSession<?> session = sessions.get(input.actorId());

        if (session == null) {
            return false;
        }

        if (!session.transportName().equals(input.transportName())) {
            return false;
        }

        return acceptTyped(session, input);
    }

    private void registerTransport(PromptTransport transport) {
        Objects.requireNonNull(transport, "transport");

        String name = Objects.requireNonNull(transport.name(), "transport.name()");

        if (name.isBlank()) {
            throw new IllegalArgumentException("transport name cannot be blank.");
        }

        PromptTransport previous = transports.putIfAbsent(name, transport);

        if (previous != null) {
            throw new IllegalArgumentException("Duplicate prompt transport: " + name);
        }

        transport.bind(this);
    }

    private Optional<PromptTransport> selectTransport(Prompt<?> prompt) {
        Optional<String> preferredName = prompt.transportName();

        if (preferredName.isPresent()) {
            PromptTransport transport = transports.get(preferredName.get());

            if (transport == null) {
                return Optional.empty();
            }

            if (!transport.supports(prompt)) {
                return Optional.empty();
            }

            return Optional.of(transport);
        }

        for (PromptTransport transport : transports.values()) {
            if (!transport.supports(prompt)) {
                continue;
            }

            return Optional.of(transport);
        }

        return Optional.empty();
    }

    private <T> void scheduleTimeout(DefaultPromptSession<T> session) {
        Optional<Duration> timeout = session.prompt().timeout();

        if (timeout.isEmpty()) {
            return;
        }

        PromptTask task = scheduler.schedule(() -> finish(session, PromptResult.timeout()), timeout.get());
        session.timeoutTask(task);
    }

    private <T> void sendPromptMessage(DefaultPromptSession<T> session, PromptTransport transport) {
        if (transport.presentsPrompt()) {
            return;
        }

        PromptText message = session.prompt().message();

        if (message.empty()) {
            return;
        }

        messenger.send(session.actorId(), message);
    }

    private boolean invalidTimeout(Duration timeout) {
        Objects.requireNonNull(timeout, "timeout");

        return timeout.isNegative() || timeout.isZero();
    }

    @SuppressWarnings("unchecked")
    private <T> boolean acceptTyped(DefaultPromptSession<?> rawSession, PromptInput input) {
        DefaultPromptSession<T> session = (DefaultPromptSession<T>) rawSession;
        ParseResult<T> parsed;

        try {
            parsed = session.prompt().parser().parse(input.plainText());
        } catch (Throwable throwable) {
            finish(session, PromptResults.failed("Prompt parser failed.", throwable));
            return true;
        }

        if (parsed == null) {
            messenger.send(session.actorId(), DEFAULT_INVALID_INPUT);
            return true;
        }

        if (!parsed.successful()) {
            PromptText errorMessage = parsed.errorMessage().orElse(DEFAULT_INVALID_INPUT);

            if (!errorMessage.empty()) {
                messenger.send(session.actorId(), errorMessage);
            }

            return true;
        }

        T value = parsed.value().orElse(null);
        finish(session, PromptResult.success(value));
        return true;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void finish(DefaultPromptSession<?> session, PromptResult<?> result) {
        Objects.requireNonNull(session, "session");
        Objects.requireNonNull(result, "result");

        boolean removed = sessions.remove(session.actorId(), session);

        if (!removed) {
            return;
        }

        try {
            closeTransport(session);
        } finally {
            ((DefaultPromptSession) session).complete(result);
        }
    }

    private void closeTransport(DefaultPromptSession<?> session) {
        PromptTransport transport = transports.get(session.transportName());

        if (transport == null) {
            return;
        }

        transport.close(session);
    }

    private static <T> CompletionStage<PromptResult<T>> completed(PromptResult<T> result) {
        return CompletableFuture.completedFuture(result);
    }

    public static final class Builder {

        private final List<PromptTransport> transports = new ArrayList<>();
        private PromptScheduler scheduler;
        private PromptMessenger messenger;
        private PromptActorResolver actorResolver;

        private Builder() {
        }

        public Builder scheduler(PromptScheduler scheduler) {
            this.scheduler = Objects.requireNonNull(scheduler, "scheduler");
            return this;
        }

        public Builder messenger(PromptMessenger messenger) {
            this.messenger = Objects.requireNonNull(messenger, "messenger");
            return this;
        }

        public Builder actorResolver(PromptActorResolver actorResolver) {
            this.actorResolver = Objects.requireNonNull(actorResolver, "actorResolver");
            return this;
        }

        public Builder transport(PromptTransport transport) {
            transports.add(Objects.requireNonNull(transport, "transport"));
            return this;
        }

        public Builder transports(Iterable<? extends PromptTransport> transports) {
            Objects.requireNonNull(transports, "transports");

            for (PromptTransport transport : transports) {
                transport(transport);
            }

            return this;
        }

        public Builder transports(PromptTransport... transports) {
            Objects.requireNonNull(transports, "transports");

            for (PromptTransport transport : transports) {
                transport(transport);
            }

            return this;
        }

        public DefaultPromptService build() {
            if (scheduler == null) {
                throw new IllegalStateException("scheduler is required.");
            }

            if (messenger == null) {
                throw new IllegalStateException("messenger is required.");
            }

            if (actorResolver == null) {
                throw new IllegalStateException("actorResolver is required.");
            }

            return new DefaultPromptService(this);
        }

    }

}