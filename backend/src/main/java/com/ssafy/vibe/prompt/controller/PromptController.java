package com.ssafy.vibe.prompt.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.vibe.auth.domain.UserPrincipal;
import com.ssafy.vibe.common.schema.BaseResponse;
import com.ssafy.vibe.prompt.controller.request.PostGenerateRequest;
import com.ssafy.vibe.prompt.controller.request.PromptSaveRequest;
import com.ssafy.vibe.prompt.controller.request.PromptUpdateRequest;
import com.ssafy.vibe.prompt.controller.response.CreatedPostResponse;
import com.ssafy.vibe.prompt.controller.response.OptionResponse;
import com.ssafy.vibe.prompt.controller.response.RetrievePromptResponse;
import com.ssafy.vibe.prompt.service.PromptService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(
	name = "Prompt Controller",
	description = "프롬프트"
)
@RestController
@RequestMapping("/api/v1/prompt")
@RequiredArgsConstructor
@Slf4j
public class PromptController {

	private final PromptService promptService;

	@Operation(
		summary = "✅AI 포스트 초안 생성",
		description = """
			promptId를 통해 AI 포스트 초안을 생성합니다.
			포스트 초안 생성 시 프롬프트에 저장된 userComment, userAIProviderId는 null일 수 없습니다.
			"""
	)
	@PostMapping("/ai-post")
	public ResponseEntity<BaseResponse<CreatedPostResponse>> createDraft(
		@AuthenticationPrincipal UserPrincipal userPrincipal,
		@Valid @RequestBody PostGenerateRequest postGenerateRequest
	) {
		CreatedPostResponse draftPost = promptService.createDraft(userPrincipal.getUserId(),
			postGenerateRequest.toCommand());
		return ResponseEntity.ok(BaseResponse.success(draftPost));
	}

	@Operation(
		summary = "✅프롬프트 저장",
		description = "프롬프트 데이터를 받아 저장합니다. templateId는 null일 수 없습니다."
	)
	@PostMapping
	public ResponseEntity<BaseResponse<Void>> savePrompt(
		@AuthenticationPrincipal UserPrincipal userPrincipal,
		@Valid @RequestBody PromptSaveRequest promptRequest
	) {
		Long userId = userPrincipal.getUserId();
		promptService.savePrompt(userId, promptRequest.toCommand());
		return ResponseEntity.ok(BaseResponse.success(null));
	}

	@Operation(
		summary = "✅프롬프트 단건 조회",
		description = "저장한 프롬프트 내용을 조회합니다."
	)
	@GetMapping("/{promptId}")
	public ResponseEntity<BaseResponse<RetrievePromptResponse>> getPrmopt(
		@AuthenticationPrincipal UserPrincipal userPrincipal,
		@PathVariable("promptId") Long promptId
	) {
		Long userId = userPrincipal.getUserId();
		RetrievePromptResponse retrievePromptResponse = promptService.getPrompt(userId, promptId);
		return ResponseEntity.ok(BaseResponse.success(retrievePromptResponse));
	}

	@Operation(
		summary = "✅프롬프트 수정",
		description = "프롬프트 데이터를 받아 수정합니다. templateId는 null일 수 없습니다."
	)
	@PutMapping("/{promptId}")
	public ResponseEntity<BaseResponse<Void>> updatePrompt(
		@AuthenticationPrincipal UserPrincipal userPrincipal,
		@PathVariable("promptId") Long promptId,
		@Valid @RequestBody PromptUpdateRequest promptupdateRequest
	) {
		Long userId = userPrincipal.getUserId();
		promptService.updatePrompt(userId, promptId, promptupdateRequest.toCommand());

		return ResponseEntity.ok(BaseResponse.success(null));
	}

	@Operation(
		summary = "✅사용 가능한 옵션 목록 조회",
		description = "설정창/프롬프트 생성 시 활용될 옵션 목록을 조회합니다."
	)
	@GetMapping("/option")
	public ResponseEntity<BaseResponse<List<OptionResponse>>> getOptionList(
		@AuthenticationPrincipal UserPrincipal userPrincipal
	) {
		List<OptionResponse> options = promptService.getOptionList();
		return ResponseEntity.ok(BaseResponse.success(options));
	}

	@Operation(
		summary = "✅프롬프트 삭제",
		description = "promptId를 통해 프롬프트를 삭제합니다."
	)
	@DeleteMapping("/{promptId}")
	public ResponseEntity<BaseResponse<Void>> deletePrompt(
		@AuthenticationPrincipal UserPrincipal userPrincipal,
		@PathVariable("promptId") Long promptId
	) {
		Long userId = userPrincipal.getUserId();
		promptService.deletePrompt(userId, promptId);

		return ResponseEntity.ok(BaseResponse.success(null));
	}
}
