package com.ssafy.vibe.user.helper;

import org.springframework.stereotype.Component;

import com.ssafy.vibe.common.exception.BadRequestException;
import com.ssafy.vibe.common.exception.ExceptionCode;
import com.ssafy.vibe.user.domain.UserEntity;
import com.ssafy.vibe.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserHelper {

	private final UserRepository userRepository;

	public UserEntity getUser(Long userId) {
		return userRepository.findById(userId)
			.orElseThrow(() -> new BadRequestException(ExceptionCode.USER_NOT_FOUND));
	}
}
