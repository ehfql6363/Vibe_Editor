package com.ssafy.vibe.template.controller.response;

import java.util.List;

import com.ssafy.vibe.template.domain.TemplateEntity;
import com.ssafy.vibe.template.service.dto.TemplateDTO;

import lombok.Builder;

@Builder
public record TemplateListResponse(
	List<TemplateDTO> templates
) {
	public static TemplateListResponse from(List<TemplateEntity> templates) {
		return TemplateListResponse
			.builder()
			.templates(templates.stream().map(TemplateDTO::from).toList())
			.build();
	}
}
