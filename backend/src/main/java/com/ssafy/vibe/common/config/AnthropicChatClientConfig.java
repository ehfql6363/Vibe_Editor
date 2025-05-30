package com.ssafy.vibe.common.config;

import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AnthropicChatClientConfig {

	@Bean
	public ChatClient anthropicChatClient(AnthropicChatModel anthropicChatModel) {
		return ChatClient.create(anthropicChatModel);
	}
}
