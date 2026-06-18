package io.github.silentdevelopment.prompts.transport;

import io.github.silentdevelopment.prompts.Prompt;
import io.github.silentdevelopment.prompts.session.PromptSession;

public interface PromptTransport {

    String name();

    boolean supports(Prompt<?> prompt);

    void bind(PromptInputSink sink);

    void open(PromptSession<?> session);

    void close(PromptSession<?> session);

    void shutdown();

    default boolean presentsPrompt() {
        return false;
    }

}