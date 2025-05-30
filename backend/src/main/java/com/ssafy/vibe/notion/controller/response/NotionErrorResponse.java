package com.ssafy.vibe.notion.controller.response;

import static com.ssafy.vibe.common.exception.ExceptionCode.*;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.vibe.common.exception.ExternalAPIException;

public record NotionErrorResponse(
	String object,
	int status,
	String code,
	String message,
	@JsonProperty("request_id") String requestId
) {
	public static NotionErrorResponse from(String json) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			return objectMapper.readValue(json, NotionErrorResponse.class);
		} catch (Exception e) {
			throw new ExternalAPIException(NOTION_JSON_PARSING_ERROR);
		}
	}
}
