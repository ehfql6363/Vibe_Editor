package com.ssafy.vibe.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ExceptionCode {
	// Auth 관련
	UNSUPPORTED_PROVIDER("UNSUPPORTED_PROVIDER", "지원하지 않는 프로바이더 입니다."),
	INVALID_TOKEN("INVALID_TOKEN", "유효하지 않은 토큰입니다."),
	AUTHENTICATION_FAILED("AUTHENTICATION_FAILED", "인증에 실패했습니다."),

	// SSAFY 관련
	SSAFY_RETRIEVE_USERINFO_FAILED("SSAFY_RETRIEVE_USERINFO_FAILED", "SSAFY 사용자 조회 중 문제가 발생했습니다."),
	SSAFY_REISSUE_TOKEN_FAILED("SSAFY_REISSUE_TOKEN_FAILED", "SSAFY 토큰 재발급 중 문제가 발생했습니다."),
	SSAFY_RETRIEVE_TOKEN_FAILED("SSAFY_RETRIEVE_TOKEN_FAILED", "SSAFY 토큰 발급 중 문제가 발생했습니다."),
	SSAFY_JWT_TOKEN_REDIRECT_FAILED("SSAFY_ACCESS_TOKEN_REDIRECT_FAILED", "SSAFY JWT 리다이렉트 중 문제가 발생했습니다."),

	// User 관련
	USER_NOT_FOUND("USER_NOT_FOUND", "유저가 존재하지 않습니다."),
	DUPLICATE_EMAIL("DUPLICATE_EMAIL", "이미 존재하는 이메일입니다."),

	// Notion 관련
	INVALID_NOTION_TOKEN("INVALID_NOTION_TOKEN", "유효하지 않은 노션 API 토큰 입니다."),
	DUPLICATED_NOTION_TOKEN("DUPLICATED_NOTION_TOKEN", "이미 등록된 노션 API 토큰입니다."),
	RETRIEVE_NOTION_DATABASE_FAILED("RETRIEVE_NOTION_DATABASE_FAILED", "노션 데이터베이스 조회 중 문제가 발생했습니다."),
	INVALID_NOTION_DATABASE_UUID("INVALID_NOTION_DATABASE_UUID", "유효하지 않은 노션 데이터베이스 UUID입니다."),
	NOTION_UPLOAD_FAILED("NOTION_UPLOAD_FAILED", "노션 게시에 에러가 발생했습니다."),
	UPLOADED_NOTION_NOT_FOUND("UPLOADED_NOTION_NOT_FOUND", "게시된 노션을 찾을 수 없습니다."),
	DUPLICATED_NOTION_DATABASE_UUID("DUPLICATED_NOTION_DATABASE_UUID", "이미 등록된 노션 데이터베이스입니다."),
	NOTION_DATABASE_NOT_FOUND("NOTION_DATABASE_NOT_FOUND", "노션 데이터베이스 ID를 확인해주세요."),
	NOTION_JSON_PARSING_ERROR("NOTION_JSON_PARSING_ERROR", "응답 데이터 파싱 중 오류가 발생했습니다."),
	NOTION_INVALID_JSON("invalid_json", "요청 본문이 잘못된 JSON 형식입니다."),
	NOTION_INVALID_REQUEST_URL("invalid_request_url", "요청 URL이 잘못되었습니다."),
	NOTION_INVALID_REQUEST("invalid_request", "잘못된 요청입니다."),
	NOTION_INVALID_GRANT("invalid_grant", "OAuth 인증이 실패했습니다."),
	NOTION_VALIDATION_ERROR("validation_error", "요청 값이 유효성 검사에 실패했습니다."),
	NOTION_MISSING_VERSION("missing_version", "Notion-Version 헤더가 누락되었습니다."),
	NOTION_UNAUTHORIZED("unauthorized", "인증 정보가 없거나 유효하지 않습니다."),
	NOTION_RESTRICTED_RESOURCE("restricted_resource", "권한이 없는 리소스입니다."),
	NOTION_OBJECT_NOT_FOUND("object_not_found", "해당 리소스를 찾을 수 없습니다."),
	NOTION_CONFLICT_ERROR("conflict_error", "리소스 충돌이 발생했습니다."),
	NOTION_RATE_LIMITED("rate_limited", "노션 게시를 너무 많이 요청했습니다. 잠시 후 다시 시도해주세요."),
	NOTION_BAD_GATEWAY("bad_gateway", "게이트웨이 오류가 발생했습니다."),
	NOTION_SERVICE_UNAVAILABLE("service_unavailable", "Notion 서비스가 일시적으로 중단되었습니다."),
	NOTION_DATABASE_CONNECTION_UNAVAILABLE("database_connection_unavailable", "데이터베이스 연결이 불가능합니다."),
	NOTION_GATEWAY_TIMEOUT("gateway_timeout", "게이트웨이 응답 시간이 초과되었습니다."),
	NOTION_API_ERROR("NOTION_API_ERROR", "노션 API에 알 수 없는 오류가 발생했습니다."),

	// Post 관련
	POST_NOT_FOUND("POST_NOT_FOUND", "포스트가 존재하지 않습니다."),
	POST_NOT_VALID("POST_NOT_VALID", "해당 포스트에 접근할 수 없습니다."),
	// Template 관련
	TEMPLATE_NOT_FOUND("TEMPLATE_NOT_FOUND", "템플릿이 존재하지 않습니다."),

	//암호화 관련
	ENCRYPT_ERROR("ENCRYPT_ERROR", "암호화에 실패했습니다."),
	DECRYPTED_ERROR("DECRYPTED_ERROR", "복호화에 실패했습니다."),

	// Snapshot 관련
	SNAPSHOT_NOT_FOUND("SNAPSHOT_NOT_FOUND", "스냅샷이 존재하지 않습니다."),

	// Prompt, PromptAttach, PromptOption 관련
	PROMPT_NOT_FOUND("PROMPT_NOT_FOUND", "프롬프트가 존재하지 않습니다."),
	PROMPT_ATTACH_NOT_FOUND("PROMPT_ATTACH_NOT_FOUND", "스냅샷&설명 첨부 자료가 존재하지 않습니다."),
	OPTION_NOT_FOUND("OPTION_NOT_FOUND", "옵션이 존재하지 않습니다."),
	OWNER_MISMATCH("OWNER_MISMATCH", "프롬프트 작성자가 아닙니다."),
	TEMPLATE_MISMATCH("TEMPLATE_MISMATCH", "템플릿 ID를 확인해주세요."),
	PROMPT_CONTENT_NULL("PROMPT_CONTENT_NULL", "초안 생성을 위한 내용(유저 코멘트)이 비어있습니다."),
	USER_AI_PROVIDER_NOT_FOUND("USER_AI_PROVIDER_NOT_FOUND", "등록되지 않은 AI 프로바이더입니다."),
	USER_AI_PROVIDER_NULL("USER_AI_PROVIDER_NULL", "등록된 AI 프로바이더가 존재하지 않습니다. 포스트 초안 생성을 위해 AI 프로바이더를 등록해주세요."),

	// Post 관련
	POST_GENERATE_FAILED("POST_GENERATE_FAILED", "포스트 생성에 실패했습니다."),

	// Anthropic 관련
	CLAUDE_INVALID_REQUEST_ERROR("INVALID_REQUEST_ERROR", "CLAUDE 요청의 형식이나 내용에 문제가 있습니다."),
	CLAUDE_AUTHENTICATION_ERROR("AUTHENTICATION_ERROR", "CLAUDE API 키에 문제가 있습니다."),
	CLAUDE_PERMISSION_ERROR("PERMISSION_ERROR", "CLAUDE API 키에는 지정된 리소스를 사용할 권한이 없습니다."),
	CLAUDE_NOT_FOUND_ERROR("CLAUDE_NOT_FOUND_ERROR", "CLAUDE에 요청한 리소스를 찾을 수 없습니다."),
	CLAUDE_REQUEST_TOO_LARGE("CLAUDE_REQUEST_TOO_LARGE", "CLAUDE에 요청이 허용된 최대 바이트 수를 초과했습니다."),
	CLAUDE_RATE_LIMIT_ERROR("CLAUDE_RATE_LIMIT_ERROR", "사용자 CLAUDE 계정이 요금 한도에 도달했습니다."),
	CLAUDE_API_ERROR("CLAUDE_API_ERROR", "Anthropic 시스템 내부에 예기치 않은 오류가 발생했습니다."),
	CLAUDE_OVERLOADED_ERROR("CLAUDE_OVERLOADED_ERROR", "Anthropic의 API가 일시적으로 과부하되었습니다."),
	CLAUDE_EMPTY_CONTENT("CLAUDE_EMPTY_CONTENT", "Claude API 응답에 콘텐츠가 없습니다."),
	CLAUDE_JSON_PARSING_ERROR("CLAUDE_JSON_PARSING_ERROR", "응답 데이터 파싱 중 오류가 발생했습니다."),
	CLAUDE_REQUEST_DATA_NOT_FOUND("CLAUDE_REQUEST_DATA_NOT_FOUND", "Claude API 응답에서 요청한 데이터 형식을 찾을 수 없습니다."),
	CLAUDE_OVER_MAX_TOKEN("CLAUDE_OVER_MAX_TOKEN", "최대 토큰 수를 초과했습니다."),

	// 사용자 AI
	AI_BRAND_NOT_FOUND("AI_BRAND_NOT_FOUND", "지원하지 않는 AI 브랜드입니다."),
	AI_MODEL_NOT_FOUND("AI_MODEL_NOT_FOUND", "AI 모델이 존재하지 않습니다."),
	DUPLICATED_AI_BRAND("DUPLICATED_AI_MODEL", "AI 브랜드는 하나만 등록 가능합니다."),

	// OpenAI 관련
	OPENAI_BAD_REQUEST_ERROR("OPENAI_BAD_REQUEST_ERROR", "OpenAI API 요청의 형식이나 내용에 문제가 있습니다."),
	OPENAI_UNAUTHORIZED_ERROR("OPENAI_AUTHENTICATION_ERROR", "OpenAI API 키에 문제가 있습니다."),
	OPENAI_PERMISSION_DENIED_ERROR("OPENAI_PERMISSION_ERROR", "OpenAI API 키에는 지정된 리소스를 사용할 권한이 없습니다."),
	OPENAI_NOT_FOUND_ERROR("OPENAI_NOT_FOUND_ERROR", "OpenAI에 요청한 리소스를 찾을 수 없습니다."),
	OPENAI_UNPROCESSABLE_ENTITY_ERROR("OPENAI_UNPROCESSABLE_ENTITY_ERROR", "OpenAI에 요청이 허용된 최대 바이트 수를 초과했습니다."),
	OPENAI_RATE_LIMIT_ERROR("OPENAI_RATE_LIMIT_ERROR", "사용자 OpenAI 계정이 요금 한도에 도달했습니다."),
	OPENAI_INTERNAL_SERVER_ERROR("OPENAI_INTERNAL_SERVER_ERROR", "OpenAI 시스템 내부에 예기치 않은 오류가 발생했습니다."),
	OPENAI_API_ERROR("OPENAI_API_ERROR", "OpenAI 시스템에 예기치 않은 오류가 발생했습니다."),
	OPENAI_IO_ERROR("OPENAI_IO_ERROR", "OpenAI API 응답 중 네트워크 오류가 발생했습니다."),
	OPENAI_INVALID_DATA_ERROR("OPENAI_INVALID_DATA_ERROR", "OpenAI API 응답 데이터가 유효하지 않습니다."),
	OPENAI_EMPTY_CONTENT("OPENAI_EMPTY_CONTENT", "OpenAI API 응답에 콘텐츠가 없습니다."),
	OPENAI_REQUEST_DATA_NOT_FOUND("OPENAI_REQUEST_DATA_NOT_FOUND", "OpenAI API 응답에서 요청한 데이터 형식을 찾을 수 없습니다."),
	OPENAI_JSON_PARSING_ERROR("OPENAI_JSON_PARSING_ERROR", "응답 데이터 파싱 중 오류가 발생했습니다.");

	private final String code;
	private final String message;

	public static ExceptionCode fromCode(String code) {
		for (ExceptionCode e : values()) {
			if (e.code.equalsIgnoreCase(code)) {
				return e;
			}
		}
		return null;
	}
}


