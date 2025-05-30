package com.ssafy.vibe.common.aop;

import java.util.Arrays;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Aspect
@Slf4j
@Component
public class ServiceTime {

	private static final String SERVICE_LOG_PREFIX = "[서비스]";
	private static final String SERVICE_ERROR_LOG_PREFIX = "[ERROR]";
	private static final String START_SEPARATOR = "▶▶";
	private static final String END_SEPARATOR = "◀◀";
	private static final String EXCEPTION_SEPARATOR = "💥💥";
	private static final String LINE_SEPARATOR = "============================================================";

	@Around("execution(* com.ssafy.vibe..service..*(..))")
	public Object serviceTime(ProceedingJoinPoint joinPoint) throws Throwable {
		String fullPathClassName = joinPoint.getSignature().getDeclaringTypeName();
		String className = fullPathClassName.substring(fullPathClassName.lastIndexOf(".") + 1);
		String methodName = className + "." + joinPoint.getSignature().getName();

		Object[] args = joinPoint.getArgs();
		long startTime = System.currentTimeMillis();

		StringBuilder startLog = new StringBuilder();
		startLog.append("\n").append(START_SEPARATOR).append(SERVICE_LOG_PREFIX).append(START_SEPARATOR).append("\n")
			.append("▶ [Method]   : ").append(methodName).append("\n")
			.append("▶ [Params]   : ").append(args.length > 0 ? Arrays.toString(args) : "No parameters").append("\n")
			.append(LINE_SEPARATOR);
		log.debug(startLog.toString());

		Object result = joinPoint.proceed();

		long endTime = System.currentTimeMillis();
		long executionTime = endTime - startTime;

		StringBuilder endLog = new StringBuilder();
		endLog.append("\n").append(END_SEPARATOR).append(SERVICE_LOG_PREFIX).append(END_SEPARATOR).append("\n")
			.append("▶ [Method]   : ").append(methodName).append("\n")
			.append("▶ [실행시간]  : ").append(executionTime).append(" ms").append("\n")
			// .append("▶ [Response] : ").append(result != null ? result : "리턴 값 없음").append("\n")
			.append(LINE_SEPARATOR).append("\n");
		log.debug(endLog.toString());

		return result;
	}

	// 예외가 모두 발생하고 나서 잡음
	@AfterThrowing(
		pointcut = "(execution(* com.ssafy.vibe..service..*(..)) || " +
			"execution(* com.ssafy.vibe..controller..*.*(..)) || " +
			"execution(* com.ssafy.vibe..repository..*.*(..)))",
		throwing = "e"
	)
	public void logServiceException(Throwable e) {
		HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
		StackTraceElement stackTraceElement = e.getStackTrace()[0];
		String fullPathClassName = stackTraceElement.getClassName();
		String className = fullPathClassName.substring(fullPathClassName.lastIndexOf(".") + 1);
		String methodName = className + "." + stackTraceElement.getMethodName();

		String layer = "Other";
		if (fullPathClassName.contains(".controller.")) {
			layer = "Controller";
		} else if (fullPathClassName.contains(".service.")) {
			layer = "Service";
		} else if (fullPathClassName.contains(".repository.")) {
			layer = "Repository";
		}

		StringBuilder exceptionLog = new StringBuilder();
		String exceptionMessage = e.getMessage();
		exceptionLog.append("\n")
			.append(EXCEPTION_SEPARATOR)
			.append(SERVICE_ERROR_LOG_PREFIX)
			.append(EXCEPTION_SEPARATOR)
			.append("\n")
			.append("▶ [Layer]         : ").append(layer).append("\n") // 레이어 정보 추가
			.append("▶ [HTTP Method]   : ").append(request.getMethod()).append("\n")
			.append("▶ [Request URI]   : ").append(request.getRequestURI()).append("\n")
			.append("▶ [Method]        : ").append(methodName).append("\n")
			.append("▶ [Exception-Find]: ").append(e.getClass().getSimpleName()).append("\n")
			.append("▶ [Message]       : ")
			.append(exceptionMessage == null ? "예외 메시지 없음" : exceptionMessage)
			.append("\n")
			.append(LINE_SEPARATOR);
		log.error(exceptionLog.toString(), e);
	}
}
