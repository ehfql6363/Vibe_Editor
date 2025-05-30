package com.ssafy.vibe.common.exception;

import lombok.Getter;

@Getter
public class ExternalAPIException extends BaseException {

	public ExternalAPIException(ExceptionCode exceptionCode) {
		super(exceptionCode);
	}

	public ExternalAPIException(String message) {
		super("BAD_GATEWAY", message);
	}
	
	// public ExternalAPIException(int code, ExceptionCode exceptionCode) {}
}
