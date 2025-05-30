package com.ssafy.vibe.common.exception;

import lombok.Getter;

@Getter
public class AuthenticationException extends BaseException {

	public AuthenticationException(ExceptionCode exceptionCode) { super(exceptionCode); }

	public AuthenticationException(String message) {
		super("INVALID_AUTHENTICATION", message);
	}
}
