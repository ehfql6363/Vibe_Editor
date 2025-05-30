package com.ssafy.vibe.template.service.dto;

import java.time.Instant;

import com.ssafy.vibe.template.domain.TemplateEntity;

import lombok.Builder;

@Builder
public record TemplateDTO(
	Long templateId,
	String templateName,
	Instant createdAt,
	Instant updatedAt
) {
	public static TemplateDTO from(TemplateEntity template) {
		return TemplateDTO.builder()
			.templateId(template.getId())
			.templateName(template.getTemplateName())
			.createdAt(template.getCreatedAt())
			.updatedAt(template.getUpdatedAt())
			.build();
	}
}
