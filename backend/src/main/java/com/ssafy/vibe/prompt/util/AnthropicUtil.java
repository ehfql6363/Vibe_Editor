package com.ssafy.vibe.prompt.util;

import static com.ssafy.vibe.common.exception.ExceptionCode.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.anthropic.client.AnthropicClient;
import com.anthropic.client.okhttp.AnthropicOkHttpClient;
import com.anthropic.core.http.HttpResponseFor;
import com.anthropic.models.messages.ContentBlock;
import com.anthropic.models.messages.Message;
import com.anthropic.models.messages.MessageCountTokensParams;
import com.anthropic.models.messages.MessageCreateParams;
import com.anthropic.models.messages.MessageTokensCount;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.vibe.common.exception.BadRequestException;
import com.ssafy.vibe.common.exception.ExternalAPIException;
import com.ssafy.vibe.common.exception.ServerException;
import com.ssafy.vibe.prompt.controller.response.AnthropicErrorResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class AnthropicUtil {
	private static final ObjectMapper mapper = new ObjectMapper();

	@Value("${spring.ai.anthropic.chat.options.temperature}")
	private float anthropicTemperature;
	@Value("${spring.ai.anthropic.chat.options.max-tokens}")
	private int anthropicMaxTokens;

	public HttpResponseFor<Message> callClaudeAPI(
		String model, Double temperature,
		String apiKey,
		String systemPromptContent, String userPromptContent
	) {
		AnthropicClient client = AnthropicOkHttpClient.builder()
			.apiKey(apiKey)
			.build();

		MessageCountTokensParams tokensParams = MessageCountTokensParams.builder()
			.model(model)
			.system(systemPromptContent)
			.addUserMessage(userPromptContent)
			.build();

		MessageTokensCount inputTokenCount = client.messages().countTokens(tokensParams);
		Long finalInputTokenCount = inputTokenCount.inputTokens() * 4L;

		MessageCreateParams params = MessageCreateParams.builder()
			.maxTokens(finalInputTokenCount)
			.model(model)
			.system(systemPromptContent)
			.addUserMessage(userPromptContent)
			.temperature(temperature)
			.build();

		return client.messages().withRawResponse().create(params);
	}

	public String[] handleClaudeResponse(HttpResponseFor<Message> response) {
		int statusCode = response.statusCode();

		if (statusCode == 200) {
			Message message = response.parse();

			if ("max_tokens".equals(message._stopReason().toString())) {
				throw new ExternalAPIException(CLAUDE_OVER_MAX_TOKEN);
			}

			List<ContentBlock> contentBlocks = message.content();
			if (contentBlocks.isEmpty()) {
				throw new ExternalAPIException(CLAUDE_EMPTY_CONTENT);
			}

			String content = contentBlocks.getFirst().toString();
			return parseContent(content);
		}

		try {
			String errorJson = new String(response.body().readAllBytes(), StandardCharsets.UTF_8);
			String parsedErrorMsg = parseAnthropicErrorMessage(errorJson);
			log.error("Claude API Error - {}", parsedErrorMsg);
			if (parsedErrorMsg.contains("max_tokens")) {
				throw new ExternalAPIException(CLAUDE_OVER_MAX_TOKEN);
			}
		} catch (IOException e) {
			// InputStream을 JSON으로 파싱 실패
			throw new ExternalAPIException(CLAUDE_JSON_PARSING_ERROR);
		}

		switch (statusCode) {
			case 400 -> throw new BadRequestException(CLAUDE_INVALID_REQUEST_ERROR);
			case 401 -> throw new BadRequestException(CLAUDE_AUTHENTICATION_ERROR);
			case 403 -> throw new BadRequestException(CLAUDE_PERMISSION_ERROR);
			case 404 -> throw new BadRequestException(CLAUDE_NOT_FOUND_ERROR);
			case 413 -> throw new BadRequestException(CLAUDE_REQUEST_TOO_LARGE);
			case 429 -> throw new BadRequestException(CLAUDE_RATE_LIMIT_ERROR);
			case 529 -> throw new ExternalAPIException(CLAUDE_OVERLOADED_ERROR);
			default -> throw new ExternalAPIException(CLAUDE_API_ERROR);
		}
	}

	private String parseAnthropicErrorMessage(String responseBody) throws JsonProcessingException {
		AnthropicErrorResponse errorResponse = mapper.readValue(responseBody, AnthropicErrorResponse.class);
		return errorResponse.getError().getMessage();
	}

	private String[] parseContent(String content) {
		Pattern pattern = Pattern.compile("```json\\s*(\\{.*?})\\s*```", Pattern.DOTALL);
		Matcher matcher = pattern.matcher(content);

		if (!matcher.find()) {
			throw new ExternalAPIException(CLAUDE_REQUEST_DATA_NOT_FOUND);
		}

		JsonNode responseJson = null;
		try {
			responseJson = mapper.readTree(matcher.group(1));
		} catch (JsonProcessingException e) {
			log.error("JSON 파싱 오류: {}", e.getMessage());
			throw new ServerException(CLAUDE_JSON_PARSING_ERROR);
		}

		String postTitle = responseJson.get("postTitle").asText();
		String postContent = responseJson.get("postContent").asText();

		return new String[] {postTitle, postContent};
	}
}
