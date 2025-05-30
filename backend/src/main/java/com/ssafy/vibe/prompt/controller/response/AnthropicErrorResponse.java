package com.ssafy.vibe.prompt.controller.response;

import lombok.Getter;

@Getter
public class AnthropicErrorResponse {
	private String type;
	private ErrorDetail error;

	@Getter
	public static class ErrorDetail {
		private String type;
		private String message;
	}
}