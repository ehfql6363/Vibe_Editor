package com.ssafy.vibe.prompt.util;

import static com.ssafy.vibe.common.exception.ExceptionCode.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.core.http.HttpResponseFor;
import com.openai.errors.OpenAIInvalidDataException;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import com.openai.models.chat.completions.ChatCompletionMessage;
import com.ssafy.vibe.common.exception.BadRequestException;
import com.ssafy.vibe.common.exception.ExternalAPIException;
import com.ssafy.vibe.common.exception.ServerException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class OpenAIUtil {
	private static final ObjectMapper mapper = new ObjectMapper();

	public HttpResponseFor<ChatCompletion> callOpenAIAPI(
		String model, Double temperature,
		String apiKey,
		String systemPromptContent, String userPromptContent
	) {
		OpenAIClient client = OpenAIOkHttpClient.builder()
			.apiKey(apiKey)
			.build();

		ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
			.model(model)
			// .responseFormat() JSON 포맷 설정 필요할 수 있음
			.addSystemMessage(systemPromptContent)
			.addUserMessage(userPromptContent)
			.temperature(temperature)
			.build();

		// TODO: 이 단계에서 예외가 터지므로 에러 핸들링 필요
		return client.chat().completions().withRawResponse().create(params);
	}

	public String[] handleOpenAIResponse(HttpResponseFor<ChatCompletion> response) {
		int statusCode = response.statusCode();

		if (statusCode == 200) {
			try {
				ChatCompletion chatCompletion = response.parse();
				return chatCompletion.choices().stream()
					.map(ChatCompletion.Choice::message)
					.map(ChatCompletionMessage::content) // Optional<String>
					.filter(Optional::isPresent)
					.map(Optional::get)                  // String
					.findFirst()
					.map(this::parseContent)
					.orElseThrow(() -> new ExternalAPIException(OPENAI_EMPTY_CONTENT));
			} catch (OpenAIInvalidDataException e) {
				throw new ExternalAPIException(OPENAI_INVALID_DATA_ERROR);
			}
		}

		try {
			String errorJson = new String(response.body().readAllBytes(), StandardCharsets.UTF_8);
			log.error("OpenAI API Error - {}", errorJson);
		} catch (IOException e) {
			throw new ExternalAPIException(OPENAI_JSON_PARSING_ERROR);
		}

		switch (statusCode) {
			case 400 -> throw new BadRequestException(OPENAI_BAD_REQUEST_ERROR);
			case 401 -> throw new BadRequestException(OPENAI_UNAUTHORIZED_ERROR);
			case 403 -> throw new BadRequestException(OPENAI_PERMISSION_DENIED_ERROR);
			case 404 -> throw new BadRequestException(OPENAI_NOT_FOUND_ERROR);
			case 422 -> throw new BadRequestException(OPENAI_UNPROCESSABLE_ENTITY_ERROR);
			case 429 -> throw new BadRequestException(OPENAI_RATE_LIMIT_ERROR);
			default -> throw mapUnexpectedStatusCode(statusCode);
		}
	}

	private String[] parseContent(String content) {
		Pattern pattern = Pattern.compile("```json\\s*(\\{.*?})\\s*```", Pattern.DOTALL);
		Matcher matcher = pattern.matcher(content);

		if (!matcher.find()) {
			throw new ExternalAPIException(OPENAI_REQUEST_DATA_NOT_FOUND);
		}

		String rawJsonBlock = matcher.group(1);

		String cleanedJson = rawJsonBlock
			.replace("\t", "  ")
			.replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", "");

		try {
			JsonNode responseJson = mapper.readTree(cleanedJson);

			String postTitle = responseJson.path("postTitle").asText(null);
			String postContent = responseJson.path("postContent").asText(null);

			if (postTitle == null || postContent == null) {
				throw new ExternalAPIException(OPENAI_REQUEST_DATA_NOT_FOUND);
			}

			return new String[] {postTitle, postContent};
		} catch (JsonProcessingException e) {
			log.error("JSON 파싱 오류: {}", e.getMessage());
			throw new ServerException(OPENAI_JSON_PARSING_ERROR);
		}
	}

	private ExternalAPIException mapUnexpectedStatusCode(int statusCode) {
		if (statusCode >= 500 && statusCode < 600) {
			return new ExternalAPIException(OPENAI_INTERNAL_SERVER_ERROR);
		}
		return new ExternalAPIException(OPENAI_API_ERROR);
	}
}
