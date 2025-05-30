package com.ssafy.vibe.user.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.vibe.auth.domain.UserPrincipal;
import com.ssafy.vibe.common.schema.BaseResponse;
import com.ssafy.vibe.user.controller.request.UserAiCreateRequest;
import com.ssafy.vibe.user.controller.request.UserAiUpdateRequest;
import com.ssafy.vibe.user.controller.response.UserAiResponse;
import com.ssafy.vibe.user.service.UserAiProviderService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/user/ai")
public class UserAiController {

	private final UserAiProviderService userAiProviderService;

	@Operation(
		summary = "사용자 커스텀 AI 등록",
		description = "기본 제공하는 Anthropic API 외 Anthropic, OpenAI 등록 (각 1개 API key)"
	)
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<?> registerUserAPIKey(
		@AuthenticationPrincipal UserPrincipal userPrincipal,
		@Valid UserAiCreateRequest request
	) {
		userAiProviderService.registerUserAPIKey(userPrincipal.getUserId(), request);
		return ResponseEntity.ok(BaseResponse.success(null));
	}

	@Operation(
		summary = "사용자 커스텀 AI의 API Key 수정",
		description = "기본 제공하는 Anthropic 제외, 해당하는 브랜드의 API Key 수정"
	)
	@PutMapping
	public ResponseEntity<?> updateUserAPIKey(
		@AuthenticationPrincipal UserPrincipal userPrincipal,
		@Valid UserAiUpdateRequest request
	) {
		userAiProviderService.updateUserAPIKey(userPrincipal.getUserId(), request);
		return ResponseEntity.ok(BaseResponse.success(null));
	}

	@Operation(
		summary = "사용자 사용 가능 AI 조회",
		description = "기본 제공하는 Anthropic API 포함, 커스텀 AI 조회"
	)
	@GetMapping
	public ResponseEntity<?> getAiProviderList(
		@AuthenticationPrincipal UserPrincipal userPrincipal
	) {
		List<UserAiResponse> response = userAiProviderService.getAiProviderList(
			userPrincipal.getUserId()
		);
		return ResponseEntity.ok(BaseResponse.success(response));
	}
}
