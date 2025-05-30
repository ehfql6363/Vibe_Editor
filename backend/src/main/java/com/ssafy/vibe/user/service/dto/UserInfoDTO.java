package com.ssafy.vibe.user.service.dto;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import com.ssafy.vibe.user.domain.UserEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class UserInfoDTO {
	private Boolean notionActive;
	private ZonedDateTime lastLoginAt;
	private ZonedDateTime updatedAt;
	private ZonedDateTime createdAt;

	public static UserInfoDTO from(UserEntity userEntity) {
		ZoneId zone = ZoneId.of("UTC");

		return UserInfoDTO.builder()
			.notionActive(userEntity.getNotionActive())
			.lastLoginAt(userEntity.getLastLoginAt())
			.updatedAt(userEntity.getUpdatedAt().atZone(zone))
			.createdAt(userEntity.getCreatedAt().atZone(zone))
			.build();
	}
}
