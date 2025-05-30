package com.ssafy.vibe.common.exception;

import java.util.Objects;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.ssafy.vibe.common.schema.BaseResponse;
import com.ssafy.vibe.common.schema.ErrorResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class CommonExceptionHandler extends ResponseEntityExceptionHandler {

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(
		MethodArgumentNotValidException e,
		HttpHeaders headers,
		HttpStatusCode status,
		WebRequest request
	) {
		log.error("errorMessage: {}", e.getMessage());

		String errorMessage = Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage();
		ErrorResponse error = ErrorResponse.of("BAD_REQUEST", errorMessage);

		return ResponseEntity
			.status(HttpStatus.BAD_REQUEST)
			.body(BaseResponse.error(error));
	}

	@ExceptionHandler(NotFoundException.class)
	public ResponseEntity<BaseResponse<ErrorResponse>> handleNotFoundException(NotFoundException e) {
		log.error("errorMessage: {}", e.getMessage());

		ErrorResponse error = ErrorResponse.of(e.getCode(), e.getMessage());
		return ResponseEntity
			.status(HttpStatus.NOT_FOUND)
			.body(BaseResponse.error(error));
	}

	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<BaseResponse<ErrorResponse>> handleBadRequestException(BadRequestException e) {
		log.error("errorMessage: {}", e.getMessage());

		ErrorResponse error = ErrorResponse.of(e.getCode(), e.getMessage());
		return ResponseEntity
			.status(HttpStatus.BAD_REQUEST)
			.body(BaseResponse.error(error));
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<BaseResponse<ErrorResponse>> handleMethodArgumentTypeMismatchException(
		MethodArgumentTypeMismatchException e) {
		log.error("errorMessage: {}", e.getMessage());

		ErrorResponse error = ErrorResponse.of("BAD_REQUEST", "잘못된 매개변수입니다.");
		return ResponseEntity
			.status(HttpStatus.BAD_REQUEST)
			.body(BaseResponse.error(error));
	}

	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<BaseResponse<ErrorResponse>> handleAuthenticationException(AuthenticationException e) {
		log.error("Authentication 실패: {}", e.getMessage());
		ErrorResponse error = ErrorResponse.of(e.getCode(), e.getMessage());
		return ResponseEntity
			.status(HttpStatus.UNAUTHORIZED)
			.body(BaseResponse.error(error));
	}

	@ExceptionHandler(ServerException.class)
	public ResponseEntity<BaseResponse<ErrorResponse>> serverException(ServerException e) {
		log.error("외부 서버 처리 실패: {}", e.getMessage());
		ErrorResponse error = ErrorResponse.of(e.getCode(), e.getMessage());
		return ResponseEntity
			.status(HttpStatus.INTERNAL_SERVER_ERROR)
			.body(BaseResponse.error(error));
	}

	@ExceptionHandler(ExternalAPIException.class)
	public ResponseEntity<BaseResponse<ErrorResponse>> externalAPIException(ExternalAPIException e) {
		log.error("외부 API 상호작용 실패: {}", e.getMessage());
		ErrorResponse error = ErrorResponse.of(e.getCode(), e.getMessage());
		return ResponseEntity
			.status(HttpStatus.SERVICE_UNAVAILABLE)
			.body(BaseResponse.error(error));
	}

	@ExceptionHandler(ForbiddenException.class)
	public ResponseEntity<BaseResponse<ErrorResponse>> forbiddenException(ForbiddenException e) {
		log.error("사용자 권한 없음: {}", e.getMessage());
		ErrorResponse error = ErrorResponse.of(e.getCode(), e.getMessage());
		return ResponseEntity
			.status(HttpStatus.FORBIDDEN)
			.body(BaseResponse.error(error));
	}

}

