package com.ssafy.vibe.common.aop;

import java.util.Arrays;
import java.util.UUID;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
public class LogAspect {

	private static final String CONTROLLER_LOG_START = "[컨트롤러 시작]";
	private static final String CONTROLLER_LOG_END = "[컨트롤러 종료]";
	private static final String REQUEST_SEPARATOR = "";
	private static final String LINE_SEPARATOR = "----------------------------------------------------";

	private static final String MDC_REQUEST_ID = "requestId"; // MDC 키 정의

	@Before("execution(* com.ssafy.vibe..controller..*(..))")
	public void logBefore(JoinPoint joinPoint) {
		String methodName = joinPoint.getSignature().getName();
		Object[] args = joinPoint.getArgs();

		HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
		String requestId = UUID.randomUUID().toString();
		MDC.put(MDC_REQUEST_ID, requestId);

		StringBuilder logMessage = new StringBuilder();
		logMessage.append("\n")
			.append(REQUEST_SEPARATOR)
			.append(CONTROLLER_LOG_START)
			.append(REQUEST_SEPARATOR)
			.append("\n")
			.append("▶ [Request ID] : ")
			.append(requestId)
			.append("\n")
			.append("▶ [Request URI]: ")
			.append(request.getRequestURI())
			.append("\n")
			.append("▶ [Method]     : ")
			.append(methodName)
			.append("\n")
			.append("▶ [Params]     : ")
			.append(args.length > 0 ? Arrays.toString(args) : "요청값 없음")
			.append("\n")
			.append(LINE_SEPARATOR);

		log.info(logMessage.toString());
	}

	@AfterReturning(pointcut = "execution(* com.ssafy.vibe..controller..*(..))", returning = "result")
	public void logAfterMethod(JoinPoint joinPoint, Object result) {
		String methodName = joinPoint.getSignature().getName();
		String requestId = MDC.get(MDC_REQUEST_ID);

		StringBuilder logMessage = new StringBuilder();
		logMessage.append("\n").append(CONTROLLER_LOG_END).append("\n") // 구분선 및 접두사 추가
			.append("▶ [Request ID] : ").append(requestId).append("\n")
			.append("▶ [Method]     : ").append(methodName).append("\n")
			// .append("▶ [Response]   : ").append(result != null ? result : "반환값 없음").append("\n")
			.append(LINE_SEPARATOR).append("\n");

		log.info(logMessage.toString());

		MDC.remove(MDC_REQUEST_ID);
	}
}