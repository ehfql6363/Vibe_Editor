package com.ssafy.vibe.user.client.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RetrieveTokenRequest {

	private String grantType;
	private String code;

	public static RetrieveTokenRequest of(String code) {
		return new RetrieveTokenRequest("authorization_code", code);
	}
}
