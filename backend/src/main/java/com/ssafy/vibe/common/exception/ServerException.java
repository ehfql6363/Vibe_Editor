package com.ssafy.vibe.common.exception;

import lombok.Getter;

@Getter
public class ServerException extends BaseException {

	public ServerException(ExceptionCode exceptionCode) {
		super(exceptionCode);
	}

	public ServerException(String message) {
		super("SERVER_ERROR", message);
	}
}
