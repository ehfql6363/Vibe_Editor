package com.ssafy.vibe.notion.client;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.ssafy.vibe.common.exception.BadRequestException;
import com.ssafy.vibe.common.exception.ExceptionCode;
import com.ssafy.vibe.common.exception.ExternalAPIException;
import com.ssafy.vibe.common.util.Aes256Util;
import com.ssafy.vibe.notion.controller.response.NotionErrorResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotionApiClient {
	private final WebClient webClient;
	private final Aes256Util aes256Util;

	@Value("${notion.api.base_url}")
	private String NOTION_BASE_URL;
	@Value("${notion.api.version}")
	private String NOTION_VERSION;

	public Map<String, Object> createPage(Map<String, Object> pageRequest, String notionToken) {
		String notionUrl = NOTION_BASE_URL + "/pages";

		return webClient.post()
			.uri(notionUrl)
			.headers(headers -> setNotionApiHeaders(headers, notionToken))
			.contentType(MediaType.APPLICATION_JSON)
			.body(BodyInserters.fromValue(pageRequest))
			.retrieve()
			.onStatus(HttpStatusCode::isError, clientResponse ->
				clientResponse.bodyToMono(String.class).flatMap(errorBody -> {
					NotionErrorResponse error = NotionErrorResponse.from(errorBody);
					log.error("Notion API 오류: {}", error.message());
					ExceptionCode errorCode = ExceptionCode.fromCode(error.code());
					throw new ExternalAPIException(errorCode.getMessage());
				})
			)
			.bodyToMono(Map.class)
			.block();
	}

	public boolean validateNotionToken(String notionToken) {
		String notionUrl = NOTION_BASE_URL + "/users/me";

		try {
			webClient.get()
				.uri(notionUrl)
				.headers(headers -> setNotionApiHeaders(headers, notionToken))
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.bodyToMono(Void.class)
				.block();

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean validateNotionDatabase(String notionToken, String databaseUid) {
		String notionUrl = NOTION_BASE_URL + "/databases/" + databaseUid;

		try {
			webClient.get()
				.uri(notionUrl)
				.headers(headers -> setNotionApiHeaders(headers, notionToken))
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.onStatus(status -> status == HttpStatus.NOT_FOUND, // 404 Not Found 처리
					clientResponse -> {
						// 404는 유효하지 않거나 접근 불가 의미
						throw new BadRequestException(ExceptionCode.INVALID_NOTION_DATABASE_UUID);
					})
				.onStatus(status -> status == HttpStatus.UNAUTHORIZED, // 401 Unauthorized 처리
					clientResponse -> {
						throw new BadRequestException(ExceptionCode.INVALID_NOTION_TOKEN);
					})
				.bodyToMono(Void.class)
				.block();

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private void setNotionApiHeaders(HttpHeaders headers, String notionToken) {
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Bearer " + notionToken);
		headers.set("Notion-Version", NOTION_VERSION);
	}
}