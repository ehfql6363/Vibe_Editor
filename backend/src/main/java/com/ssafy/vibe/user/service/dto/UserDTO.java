package com.ssafy.vibe.user.service.dto;

import com.ssafy.vibe.user.domain.ProviderName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@AllArgsConstructor
@Getter
@Setter
public class UserDTO {
	private Long userId;
	private String userName;
	private String email;
	private ProviderName providerName;
	private String providerUid;

	public static UserDTO createUserDto(String userName, String email,
		ProviderName providerName, String providerUid) {
		return UserDTO.builder()
			.userName(userName)
			.email(email)
			.providerName(providerName)
			.providerUid(providerUid)
			.build();
	}
}