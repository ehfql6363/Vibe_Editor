package com.ssafy.vibe.user.controller.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSignupRequest {
	private String userName;
	private String email;
	private String providerName;   // PROVIDER 이름 (ex. LOCAL, GOOGLE 등)
	private String providerUid;    // 유저를 판별 가능한 식별자
}
