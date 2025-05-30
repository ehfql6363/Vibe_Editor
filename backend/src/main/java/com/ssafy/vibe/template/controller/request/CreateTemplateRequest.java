package com.ssafy.vibe.template.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateTemplateRequest(
	@NotBlank(message = "{required}")
	@Size(max = 255, message = "{max.length}")
	String templateName
) {
}