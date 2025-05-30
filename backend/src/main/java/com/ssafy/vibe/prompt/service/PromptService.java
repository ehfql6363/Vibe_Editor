package com.ssafy.vibe.prompt.service;

import java.util.List;

import com.ssafy.vibe.prompt.controller.response.CreatedPostResponse;
import com.ssafy.vibe.prompt.controller.response.OptionResponse;
import com.ssafy.vibe.prompt.controller.response.RetrievePromptResponse;
import com.ssafy.vibe.prompt.service.command.GeneratePostCommand;
import com.ssafy.vibe.prompt.service.command.PromptSaveCommand;
import com.ssafy.vibe.prompt.service.command.PromptUpdateCommand;

public interface PromptService {
	CreatedPostResponse createDraft(Long userId, GeneratePostCommand command);

	void savePrompt(Long userId, PromptSaveCommand promptCommand);

	RetrievePromptResponse getPrompt(Long userId, Long promptId);

	void updatePrompt(Long userId, Long promptId, PromptUpdateCommand promptUpdateCommand);

	List<OptionResponse> getOptionList();

	void deletePrompt(Long userId, Long promptId);
}
