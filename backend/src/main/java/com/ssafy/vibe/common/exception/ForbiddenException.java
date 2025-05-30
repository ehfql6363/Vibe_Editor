package com.ssafy.vibe.common.exception;

import lombok.Getter;

@Getter
public class ForbiddenException extends BaseException {

	public ForbiddenException(ExceptionCode exceptionCode) {
		super(exceptionCode);
	}

	public ForbiddenException(String message) {
		super("FORBIDDEN_EXCEPTION", message);
	}
}
