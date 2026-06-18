package io.github.silentdevelopment.prompts.transport;

import io.github.silentdevelopment.prompts.input.PromptInput;

public interface PromptInputSink {

    boolean accept(PromptInput input);

}