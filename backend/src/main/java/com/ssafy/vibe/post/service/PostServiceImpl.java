package com.ssafy.vibe.post.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.vibe.common.exception.BadRequestException;
import com.ssafy.vibe.common.exception.ExceptionCode;
import com.ssafy.vibe.common.exception.ForbiddenException;
import com.ssafy.vibe.common.util.Aes256Util;
import com.ssafy.vibe.notion.client.NotionApiClient;
import com.ssafy.vibe.notion.domain.NotionUploadEntity;
import com.ssafy.vibe.notion.domain.UploadStatus;
import com.ssafy.vibe.notion.factory.NotionPageRequestFactory;
import com.ssafy.vibe.notion.repository.NotionUploadRepository;
import com.ssafy.vibe.notion.util.NotionUtil;
import com.ssafy.vibe.post.controller.response.NotionPostResponse;
import com.ssafy.vibe.post.controller.response.RetrieveAiPostDetailResponse;
import com.ssafy.vibe.post.controller.response.RetrieveAiPostResponse;
import com.ssafy.vibe.post.domain.PostEntity;
import com.ssafy.vibe.post.repository.PostRepository;
import com.ssafy.vibe.post.service.command.NotionPostCommand;
import com.ssafy.vibe.post.service.command.NotionUpdateCommand;
import com.ssafy.vibe.post.service.command.PostDeleteCommand;
import com.ssafy.vibe.post.service.command.PostRetrieveDetailCommand;
import com.ssafy.vibe.post.service.dto.NotionPostDTO;
import com.ssafy.vibe.post.service.dto.PostRetrieveDTO;
import com.ssafy.vibe.post.service.dto.PostRetrieveDetailDTO;
import com.ssafy.vibe.user.domain.UserEntity;
import com.ssafy.vibe.user.helper.UserHelper;
import com.ssafy.vibe.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
	private final NotionApiClient notionApiClient;
	private final NotionPageRequestFactory notionPageRequestFactory;
	private final NotionUtil notionUtil;
	private final Aes256Util aes256Util;
	private final UserHelper userHelper;
	private final PostRepository postRepository;
	private final UserRepository userRepository;
	private final NotionUploadRepository notionUploadRepository;

	@Override
	public NotionPostResponse createNotionPost(NotionPostCommand command) {
		UserEntity user = userRepository.findById(command.getUserId())
			.orElseThrow(() -> new BadRequestException(ExceptionCode.USER_NOT_FOUND));
		String notionToken = aes256Util.decrypt(user.getNotionSecretKey());

		PostEntity post = postRepository.findByIdWithPromptAndNotionDatabase(command.getPostId())
			.orElseThrow(() -> new BadRequestException(ExceptionCode.POST_NOT_FOUND));
		String notionDatabaseId = post.getPrompt().getNotionDatabase().getDatabaseUid();

		try {
			List<Map<String, Object>> notionBlocks = notionUtil.parseMarkdownToNotionBlocks(post.getPostContent());
			String title = post.getPostTitle();

			Map<String, Object> pageRequest =
				notionPageRequestFactory.createPageRequest(notionDatabaseId, title, notionBlocks);

			Map<String, Object> response = notionApiClient.createPage(pageRequest, notionToken);
			String postUrl = (String)response.get("url");

			// 노션 업로드 성공 이력 저장
			NotionUploadEntity notionUpload = NotionUploadEntity.createNotionUpload(post, postUrl,
				UploadStatus.SUCCESS);
			notionUploadRepository.save(notionUpload);

			NotionPostDTO dto = new NotionPostDTO(postUrl);
			return dto.toResponse();
		} catch (Exception e) {
			log.error("노션 게시 실패: {}", e.getMessage());
			// 노션 업로드 실패 이력 저장
			NotionUploadEntity notionUpload = NotionUploadEntity.createNotionUpload(post, null,
				UploadStatus.FAIL);
			notionUploadRepository.save(notionUpload);

			throw new BadRequestException(ExceptionCode.NOTION_UPLOAD_FAILED);
		}
	}

	@Override
	public void updateNotionPost(NotionUpdateCommand command) {
		UserEntity user = userRepository.findById(command.getUserId())
			.orElseThrow(() -> new BadRequestException(ExceptionCode.USER_NOT_FOUND));

		PostEntity post = postRepository.findByIdWithPromptAndNotionDatabase(command.getPostId())
			.orElseThrow(() -> new BadRequestException(ExceptionCode.POST_NOT_FOUND));
		if (!post.getUser().getId().equals(user.getId())) {
			throw new ForbiddenException(ExceptionCode.POST_NOT_VALID);
		}

		post.updateTitleAndContent(command.getPostTitle(), command.getPostContent());
		postRepository.save(post); // 저장
	}

	@Override
	@Transactional(readOnly = true)
	public List<RetrieveAiPostResponse> retrievePostList(Long userId) {
		List<PostEntity> postList = postRepository.findAllByUserId(userId);

		List<NotionUploadEntity> notionUploadEntities = postList.stream()
			.map(post -> notionUploadRepository.findFirstByPostIdOrderByCreatedAtDesc(post.getId())
				.orElse(null))
			.toList();

		List<PostRetrieveDTO> dtos = Stream.iterate(0, i -> i < postList.size(), i -> i + 1)
			.map(i -> PostRetrieveDTO.fromEntity(postList.get(i), notionUploadEntities.get(i)))
			.toList();

		return dtos.stream()
			.map(PostRetrieveDTO::toResponse)
			.toList();
	}

	@Override
	public RetrieveAiPostDetailResponse retrievePostDetail(PostRetrieveDetailCommand command) {
		UserEntity user = userHelper.getUser(command.getUserId());
		PostEntity post = postRepository.findByIdWithPromptAndTemplate(command.getPostId())
			.orElseThrow(() -> new BadRequestException(ExceptionCode.POST_NOT_FOUND));

		if (!post.getUser().getId().equals(user.getId())) {
			throw new ForbiddenException(ExceptionCode.POST_NOT_VALID);
		}

		NotionUploadEntity notionUpload = notionUploadRepository.findFirstByPostIdOrderByCreatedAtDesc(post.getId())
			.orElse(null);

		PostRetrieveDetailDTO dto = PostRetrieveDetailDTO.fromEntity(post, notionUpload);
		return dto.toResponse();
	}

	@Override
	public void deletePost(PostDeleteCommand command) {
		UserEntity user = userHelper.getUser(command.getUserId());
		PostEntity post = postRepository.findByIdWithPromptAndTemplate(command.getPostId())
			.orElseThrow(() -> new BadRequestException(ExceptionCode.POST_NOT_FOUND));

		if (!post.getUser().getId().equals(user.getId())) {
			throw new ForbiddenException(ExceptionCode.POST_NOT_VALID);
		}

		post.delete();
		postRepository.save(post);
	}
}
