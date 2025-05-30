package com.ssafy.vibe.prompt.service;

import static com.ssafy.vibe.common.exception.ExceptionCode.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.ssafy.vibe.common.exception.BadRequestException;
import com.ssafy.vibe.user.domain.AiBrandName;

@Component
public class AiChatServiceFactory {
	private final Map<AiBrandName, AiChatService> map;

	public AiChatServiceFactory(List<AiChatService> services) {
		this.map = services.stream().collect(Collectors.toMap(
			AiChatService::getBrand,
			Function.identity()
		));
	}

	public AiChatService get(AiBrandName brand) {
		return Optional.ofNullable(map.get(brand))
			.orElseThrow(() -> new BadRequestException(AI_BRAND_NOT_FOUND));
	}
}
