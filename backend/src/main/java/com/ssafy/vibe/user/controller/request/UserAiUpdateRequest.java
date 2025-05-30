package com.ssafy.vibe.user.controller.request;

import com.ssafy.vibe.user.domain.AiBrandName;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserAiUpdateRequest(
	@NotNull(message = "{required}")
	AiBrandName brand,

	@NotEmpty(message = "{required}")
	@Size(max = 255, message = "{max.length}")
	String apiKey
) {
}
