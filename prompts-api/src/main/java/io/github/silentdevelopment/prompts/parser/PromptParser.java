package io.github.silentdevelopment.prompts.parser;

public interface PromptParser<T> {

    ParseResult<T> parse(String input);

}