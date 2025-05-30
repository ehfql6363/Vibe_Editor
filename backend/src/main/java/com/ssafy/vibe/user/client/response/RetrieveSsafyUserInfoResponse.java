package com.ssafy.vibe.user.client.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RetrieveSsafyUserInfoResponse {

	@JsonProperty("userId")
	private String userId;

	@JsonProperty("email")
	private String email;

	@JsonProperty("name")
	private String name;
}
