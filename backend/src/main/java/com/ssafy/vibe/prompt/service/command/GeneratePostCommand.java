package com.ssafy.vibe.prompt.service.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class GeneratePostCommand {
	private Long promptId;
}
