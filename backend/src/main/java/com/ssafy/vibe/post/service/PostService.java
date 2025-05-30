package com.ssafy.vibe.post.service;

import java.util.List;

import com.ssafy.vibe.post.controller.response.NotionPostResponse;
import com.ssafy.vibe.post.controller.response.RetrieveAiPostDetailResponse;
import com.ssafy.vibe.post.controller.response.RetrieveAiPostResponse;
import com.ssafy.vibe.post.service.command.NotionPostCommand;
import com.ssafy.vibe.post.service.command.NotionUpdateCommand;
import com.ssafy.vibe.post.service.command.PostDeleteCommand;
import com.ssafy.vibe.post.service.command.PostRetrieveDetailCommand;

public interface PostService {
	NotionPostResponse createNotionPost(NotionPostCommand command);

	void updateNotionPost(NotionUpdateCommand command);

	List<RetrieveAiPostResponse> retrievePostList(Long userId);

	RetrieveAiPostDetailResponse retrievePostDetail(PostRetrieveDetailCommand command);

	void deletePost(PostDeleteCommand command);
}
