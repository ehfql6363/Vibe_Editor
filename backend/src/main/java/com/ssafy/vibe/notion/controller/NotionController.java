package com.ssafy.vibe.notion.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.vibe.auth.domain.UserPrincipal;
import com.ssafy.vibe.common.schema.BaseResponse;
import com.ssafy.vibe.notion.controller.request.NotionConnectRequest;
import com.ssafy.vibe.notion.controller.request.NotionDatabaseInfoRequest;
import com.ssafy.vibe.notion.controller.response.RetrieveNotionDatabasesResponse;
import com.ssafy.vibe.notion.service.NotionService;
import com.ssafy.vibe.notion.service.command.NotionConnectInfoCommand;
import com.ssafy.vibe.notion.service.command.NotionRegisterDatabaseCommand;
import com.ssafy.vibe.notion.service.command.RetrieveNotionDatabasesCommand;
import com.ssafy.vibe.notion.service.dto.RetrieveNotionDatabasesDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(
	name = "Notion Controller",
	description = "노션 "
)
@RequestMapping("/api/v1/notion")
@RestController
@RequiredArgsConstructor
public class NotionController {

	private final NotionService notionService;

	@Operation(
		summary = "✅노션 API 키 등록",
		description = "사용자 생성한 노션 API 키 등록"
	)
	@PostMapping("/secretkey")
	public ResponseEntity<BaseResponse<Void>> saveNotionKey(
		@Valid @RequestBody NotionConnectRequest body,
		@AuthenticationPrincipal UserPrincipal principal
	) {
		NotionConnectInfoCommand command = new NotionConnectInfoCommand(
			principal.getUserId(),
			body.getNotionSecretKey()
		);
		notionService.saveNotionKey(command);
		return ResponseEntity.ok(
			BaseResponse.success(
				null
			)
		);
	}

	@Operation(
		summary = "✅사용자 노션 Database Uid 등록",
		description = "사용자의 노션 데이터베이스 Uid 값을 DB에 등록합니다."
	)
	@PostMapping("/database")
	public ResponseEntity<BaseResponse<Void>> registerNotionDatabase(
		@Valid @RequestBody NotionDatabaseInfoRequest body,
		@AuthenticationPrincipal UserPrincipal userPrincipal
	) {
		NotionRegisterDatabaseCommand command = new NotionRegisterDatabaseCommand(
			userPrincipal.getUserId(),
			body.getNotionDatabaseName(),
			body.getNotionDatabaseUid()
		);
		notionService.registerNotionDatabase(command);
		return ResponseEntity.ok(
			BaseResponse.success(
				null
			)
		);
	}

	@Operation(
		summary = "❌사용자 노션 Database 삭제",
		description = "사용자가 등록한 노션 데이터베이스를 삭제합니다."
	)
	@DeleteMapping("/database/{notionDatabaseId}")
	public ResponseEntity<BaseResponse<Void>> deleteNotionDatabase(
		@PathVariable("notionDatabaseId") Long notionDatabaseId,
		@AuthenticationPrincipal UserPrincipal principal
	) {
		// TODO : 데이터 베이스 삭제
		return ResponseEntity.ok(
			BaseResponse.success(
				null
			)
		);
	}

	@Operation(
		summary = "✅사용자 노션 Database 목록 조회",
		description = "사용자가 등록한 노션 데이터베이스 목록을 조회합니다."
	)
	@GetMapping("/database")
	public ResponseEntity<BaseResponse<List<RetrieveNotionDatabasesResponse>>> retrieveNotionDatabases(
		@AuthenticationPrincipal UserPrincipal principal
	) {
		RetrieveNotionDatabasesCommand command = new RetrieveNotionDatabasesCommand(principal.getUserId());
		List<RetrieveNotionDatabasesResponse> response = notionService.retrieveNotionDatabases(command)
			.stream().map(RetrieveNotionDatabasesDTO::toResponse).toList();

		return ResponseEntity.ok(
			BaseResponse.success(
				response
			)
		);
	}

}
