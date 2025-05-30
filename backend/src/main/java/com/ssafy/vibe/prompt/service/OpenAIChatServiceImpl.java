package com.ssafy.vibe.prompt.service;

import static com.ssafy.vibe.common.exception.ExceptionCode.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.openai.core.http.HttpResponseFor;
import com.openai.models.chat.completions.ChatCompletion;
import com.ssafy.vibe.common.exception.ExternalAPIException;
import com.ssafy.vibe.prompt.service.dto.AiChatInputDTO;
import com.ssafy.vibe.prompt.util.OpenAIUtil;
import com.ssafy.vibe.user.domain.AiBrandName;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class OpenAIChatServiceImpl implements AiChatService {

	private final OpenAIUtil openAIUtil;

	@Value("${spring.ai.openai.base-url}")
	private String baseUrl;
	@Value("${spring.ai.openai.api-key}")
	private String defaultApiKey;

	@Override
	public AiBrandName getBrand() {
		return AiBrandName.OpenAI;
	}

	@Override
	public String[] generateChat(AiChatInputDTO input) {
		HttpResponseFor<ChatCompletion> response = openAIUtil.callOpenAIAPI(
			input.model(),
			input.temperature(),
			input.isDefault() ? defaultApiKey : input.apiKey(),
			input.systemPrompt(),
			input.userPrompt()
		);

		return openAIUtil.handleOpenAIResponse(response);
	}

	@Override
	public void validateApiKey(String apiKey) {
		try {
			WebClient webClient = WebClient.builder()
				.baseUrl(baseUrl)
				.defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
				.build();

			HttpStatusCode statusCode = webClient.get()
				.uri("/models")
				.retrieve()
				.toBodilessEntity()
				.map(ResponseEntity::getStatusCode)
				.block();
			if (statusCode == null || !statusCode.is2xxSuccessful()) {
				log.error("사용자 OpenAI API 등록 중 오류: 잘못된 API key나 요청");
				throw new ExternalAPIException(OPENAI_UNAUTHORIZED_ERROR);
			}
		} catch (Exception e) {
			log.error("사용자 OpenAI API 등록 중 오류: {}", e.getMessage());
			throw new ExternalAPIException(OPENAI_UNAUTHORIZED_ERROR);
		}
	}
}
