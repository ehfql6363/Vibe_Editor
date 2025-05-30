package com.ssafy.vibe.post.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.ssafy.vibe.common.exception.BadRequestException;
import com.ssafy.vibe.common.exception.ExceptionCode;
import com.ssafy.vibe.common.exception.ForbiddenException;
import com.ssafy.vibe.common.util.Aes256Util;
import com.ssafy.vibe.notion.client.NotionApiClient;
import com.ssafy.vibe.notion.domain.NotionDatabaseEntity;
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
import com.ssafy.vibe.post.service.command.PostRetrieveDetailCommand;
import com.ssafy.vibe.prompt.domain.PromptEntity;
import com.ssafy.vibe.template.domain.TemplateEntity;
import com.ssafy.vibe.user.domain.UserEntity;
import com.ssafy.vibe.user.helper.UserHelper;
import com.ssafy.vibe.user.repository.UserRepository;

class PostServiceImplTest {

	@InjectMocks
	private PostServiceImpl postService;

	@Mock
	private NotionApiClient notionApiClient;
	@Mock
	private NotionPageRequestFactory notionPageRequestFactory;
	@Mock
	private NotionUtil notionUtil;
	@Mock
	private Aes256Util aes256Util;
	@Mock
	private UserHelper userHelper;
	@Mock
	private PostRepository postRepository;
	@Mock
	private UserRepository userRepository;
	@Mock
	private NotionUploadRepository notionUploadRepository;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		postService = new PostServiceImpl(
			notionApiClient, notionPageRequestFactory, notionUtil, aes256Util, userHelper, postRepository,
			userRepository,
			notionUploadRepository
		);
	}

	@Test
	@DisplayName("성공: 노션 포스트 생성")
	void createNotionPost_success() {
		// given
		Long userId = 1L, postId = 2L;
		NotionPostCommand command = new NotionPostCommand(userId, postId);
		UserEntity user = mock(UserEntity.class);
		when(user.getNotionSecretKey()).thenReturn("enc-token");
		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(aes256Util.decrypt(anyString())).thenReturn("dec-token");

		NotionDatabaseEntity database = mock(NotionDatabaseEntity.class);
		when(database.getDatabaseUid()).thenReturn("dbid");
		PromptEntity prompt = mock(PromptEntity.class);
		when(prompt.getNotionDatabase()).thenReturn(database);
		PostEntity post = mock(PostEntity.class);
		when(post.getPrompt()).thenReturn(prompt);
		when(post.getPostTitle()).thenReturn("title");
		when(post.getPostContent()).thenReturn("content");
		when(postRepository.findByIdWithPromptAndNotionDatabase(postId)).thenReturn(Optional.of(post));

		List<Map<String, Object>> blocks = List.of(Map.of("key", "value"));
		when(notionUtil.parseMarkdownToNotionBlocks(any())).thenReturn(blocks);
		Map<String, Object> requestMap = Map.of("some", "thing");
		when(notionPageRequestFactory.createPageRequest(any(), any(), any())).thenReturn(requestMap);
		String expectedUrl = "https://notion.so/some";
		Map<String, Object> notionApiResult = Map.of("url", expectedUrl);
		when(notionApiClient.createPage(any(), any())).thenReturn(notionApiResult);

		// when
		NotionPostResponse result = postService.createNotionPost(command);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getPostUrl()).isEqualTo(expectedUrl);
	}

	@Test
	@DisplayName("예외: 유저가 존재하지 않을 때")
	void createNotionPost_userNotFound() {
		// given
		Long userId = 99L, postId = 10L;
		NotionPostCommand command = new NotionPostCommand(userId, postId);
		when(userRepository.findById(userId)).thenReturn(Optional.empty());

		// when & then
		BadRequestException thrown = catchThrowableOfType(
			() -> postService.createNotionPost(command),
			BadRequestException.class
		);
		assertThat(thrown).isNotNull();
		assertThat(thrown.getCode()).isEqualTo(ExceptionCode.USER_NOT_FOUND.getCode());
		assertThat(thrown.getMessage()).isEqualTo(ExceptionCode.USER_NOT_FOUND.getMessage());
	}

	@Test
	@DisplayName("예외: 게시글이 존재하지 않을 때")
	void createNotionPost_postNotFound() {
		// given
		Long userId = 1L, postId = 123L;
		NotionPostCommand command = new NotionPostCommand(userId, postId);
		UserEntity user = mock(UserEntity.class);
		when(user.getNotionSecretKey()).thenReturn("enc-token");
		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(aes256Util.decrypt(anyString())).thenReturn("decrypted-token");
		when(postRepository.findByIdWithPromptAndNotionDatabase(postId)).thenReturn(Optional.empty());

		// when & then
		BadRequestException thrown = catchThrowableOfType(
			() -> postService.createNotionPost(command),
			BadRequestException.class
		);
		assertThat(thrown).isNotNull();
		assertThat(thrown.getCode()).isEqualTo(ExceptionCode.POST_NOT_FOUND.getCode());
		assertThat(thrown.getMessage()).isEqualTo(ExceptionCode.POST_NOT_FOUND.getMessage());
	}

	@Test
	@DisplayName("예외: Notion API 업로드 실패 시")
	void createNotionPost_notionApiError_fail() {
		// given
		Long userId = 1L, postId = 2L;
		NotionPostCommand command = new NotionPostCommand(userId, postId);

		UserEntity user = mock(UserEntity.class);
		when(user.getNotionSecretKey()).thenReturn("token");
		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(aes256Util.decrypt(anyString())).thenReturn("dec-token");

		NotionDatabaseEntity notiDb = mock(NotionDatabaseEntity.class);
		when(notiDb.getDatabaseUid()).thenReturn("dbid");
		PromptEntity prompt = mock(PromptEntity.class);
		when(prompt.getNotionDatabase()).thenReturn(notiDb);
		PostEntity post = mock(PostEntity.class);
		when(post.getPrompt()).thenReturn(prompt);
		when(post.getPostTitle()).thenReturn("t");
		when(post.getPostContent()).thenReturn("c");
		when(postRepository.findByIdWithPromptAndNotionDatabase(postId)).thenReturn(Optional.of(post));

		when(notionUtil.parseMarkdownToNotionBlocks(any())).thenThrow(new RuntimeException("error"));

		// when & then
		BadRequestException thrown = catchThrowableOfType(
			() -> postService.createNotionPost(command),
			BadRequestException.class
		);
		assertThat(thrown).isNotNull();
		assertThat(thrown.getCode()).isEqualTo(ExceptionCode.NOTION_UPLOAD_FAILED.getCode());
		assertThat(thrown.getMessage()).isEqualTo(ExceptionCode.NOTION_UPLOAD_FAILED.getMessage());
	}

	@Test
	@DisplayName("성공: 노션 포스트 제목/내용 수정")
	void updateNotionPost_success() {
		// given
		Long userId = 1L, postId = 2L;
		String newTitle = "수정된 제목", newContent = "수정된 내용";

		NotionUpdateCommand command = NotionUpdateCommand.builder()
			.userId(userId)
			.postId(postId)
			.postTitle(newTitle)
			.postContent(newContent)
			.build();

		UserEntity user = mock(UserEntity.class);
		when(user.getId()).thenReturn(userId);
		when(userRepository.findById(userId)).thenReturn(Optional.of(user));

		PostEntity post = mock(PostEntity.class);
		when(post.getUser()).thenReturn(user);
		when(postRepository.findByIdWithPromptAndNotionDatabase(postId)).thenReturn(Optional.of(post));

		// when & then
		assertDoesNotThrow(() -> postService.updateNotionPost(command));
		verify(post).updateTitleAndContent(newTitle, newContent);
		verify(postRepository).save(post);
	}

	@Test
	@DisplayName("예외: 유저가 존재하지 않을 때")
	void updateNotionPost_userNotFound() {
		// given
		NotionUpdateCommand command = NotionUpdateCommand.builder()
			.userId(999L)
			.postId(1L)
			.postTitle("title")
			.postContent("content")
			.build();
		when(userRepository.findById(999L)).thenReturn(Optional.empty());

		// when & then
		BadRequestException thrown = catchThrowableOfType(
			() -> postService.updateNotionPost(command),
			BadRequestException.class
		);
		assertThat(thrown.getCode()).isEqualTo(ExceptionCode.USER_NOT_FOUND.getCode());
	}

	@Test
	@DisplayName("예외: 게시글이 존재하지 않을 때")
	void updateNotionPost_postNotFound() {
		// given
		Long userId = 1L;
		NotionUpdateCommand command = NotionUpdateCommand.builder()
			.userId(userId)
			.postId(123L)
			.postTitle("title")
			.postContent("content")
			.build();

		UserEntity user = mock(UserEntity.class);
		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(postRepository.findByIdWithPromptAndNotionDatabase(123L)).thenReturn(Optional.empty());

		// when & then
		BadRequestException thrown = catchThrowableOfType(
			() -> postService.updateNotionPost(command),
			BadRequestException.class
		);
		assertThat(thrown.getCode()).isEqualTo(ExceptionCode.POST_NOT_FOUND.getCode());
	}

	@Test
	@DisplayName("예외: 본인의 글이 아닌 경우 수정 불가")
	void updateNotionPost_forbidden() {
		// given
		Long userId = 1L, postId = 10L;

		NotionUpdateCommand command = NotionUpdateCommand.builder()
			.userId(userId)
			.postId(postId)
			.postTitle("title")
			.postContent("content")
			.build();

		UserEntity user = mock(UserEntity.class);
		when(user.getId()).thenReturn(userId);
		when(userRepository.findById(userId)).thenReturn(Optional.of(user));

		UserEntity otherUser = mock(UserEntity.class);
		when(otherUser.getId()).thenReturn(999L);

		PostEntity post = mock(PostEntity.class);
		when(post.getUser()).thenReturn(otherUser);
		when(postRepository.findByIdWithPromptAndNotionDatabase(postId)).thenReturn(Optional.of(post));

		// when & then
		ForbiddenException thrown = catchThrowableOfType(
			() -> postService.updateNotionPost(command),
			ForbiddenException.class
		);
		assertThat(thrown.getCode()).isEqualTo(ExceptionCode.POST_NOT_VALID.getCode());
	}

	@Test
	@DisplayName("성공: 사용자별 포스트 목록 조회")
	void retrievePostList_success() {
		// given
		Long userId = 1L;
		PostEntity post1 = mock(PostEntity.class);
		PostEntity post2 = mock(PostEntity.class);

		when(post1.getId()).thenReturn(101L);
		when(post1.getPostTitle()).thenReturn("제목1");
		when(post1.getCreatedAt()).thenReturn(LocalDateTime.of(2024, 6, 7, 12, 0)
			.atZone(ZoneId.of("UTC"))
			.toInstant());
		when(post1.getUpdatedAt()).thenReturn(LocalDateTime.of(2024, 6, 7, 13, 0)
			.atZone(ZoneId.of("UTC"))
			.toInstant());

		when(post2.getId()).thenReturn(102L);
		when(post2.getPostTitle()).thenReturn("제목2");
		when(post2.getCreatedAt()).thenReturn(LocalDateTime.of(2024, 6, 8, 12, 0)
			.atZone(ZoneId.of("UTC"))
			.toInstant());
		when(post2.getUpdatedAt()).thenReturn(LocalDateTime.of(2024, 6, 8, 13, 0)
			.atZone(ZoneId.of("UTC"))
			.toInstant());

		List<PostEntity> mockPosts = List.of(post1, post2);
		when(postRepository.findAllByUserId(userId)).thenReturn(mockPosts);

		// when
		List<RetrieveAiPostResponse> result = postService.retrievePostList(userId);

		// then
		assertThat(result).hasSize(2);

		RetrieveAiPostResponse response1 = result.getFirst();
		assertThat(response1.getPostId()).isEqualTo(101L);
		assertThat(response1.getPostTitle()).isEqualTo("제목1");
		assertThat(response1.getCreatedAt().toLocalDateTime()).isEqualTo(java.time.LocalDateTime.of(2024, 6, 7, 12, 0));
		assertThat(response1.getUpdatedAt().toLocalDateTime()).isEqualTo(java.time.LocalDateTime.of(2024, 6, 7, 13, 0));

		RetrieveAiPostResponse response2 = result.get(1);
		assertThat(response2.getPostId()).isEqualTo(102L);
		assertThat(response2.getPostTitle()).isEqualTo("제목2");
		assertThat(response2.getCreatedAt().toLocalDateTime()).isEqualTo(java.time.LocalDateTime.of(2024, 6, 8, 12, 0));
		assertThat(response2.getUpdatedAt().toLocalDateTime()).isEqualTo(java.time.LocalDateTime.of(2024, 6, 8, 13, 0));
	}

	@Test
	@DisplayName("성공: 포스트 상세조회")
	void retrievePostDetail_success() {
		// given
		Long userId = 77L, postId = 11L;
		PostRetrieveDetailCommand command = new PostRetrieveDetailCommand(userId, postId);

		UserEntity user = mock(UserEntity.class);
		when(user.getId()).thenReturn(userId);
		when(userHelper.getUser(userId)).thenReturn(user);

		UserEntity postUser = mock(UserEntity.class);
		when(postUser.getId()).thenReturn(userId);

		TemplateEntity template = mock(TemplateEntity.class);
		when(template.getId()).thenReturn(111L);

		PromptEntity prompt = mock(PromptEntity.class);
		when(prompt.getId()).thenReturn(222L);
		when(prompt.getTemplate()).thenReturn(template);

		PostEntity post = mock(PostEntity.class);
		when(post.getId()).thenReturn(postId);
		when(post.getUser()).thenReturn(postUser);
		when(post.getPrompt()).thenReturn(prompt);
		when(post.getPostTitle()).thenReturn("제목");
		when(post.getPostContent()).thenReturn("본문");
		Instant created = ZonedDateTime.of(2024, 6, 11, 9, 0, 0, 0, ZoneId.of("UTC")).toInstant();
		Instant updated = ZonedDateTime.of(2024, 6, 11, 10, 30, 0, 0, ZoneId.of("UTC")).toInstant();
		when(post.getCreatedAt()).thenReturn(created);
		when(post.getUpdatedAt()).thenReturn(updated);

		when(postRepository.findByIdWithPromptAndTemplate(postId)).thenReturn(Optional.of(post));

		NotionUploadEntity notionUpload = NotionUploadEntity.createNotionUpload(post, "https://notion.so/my",
			UploadStatus.SUCCESS);
		when(notionUploadRepository.findFirstByPostIdOrderByCreatedAtDesc(postId)).thenReturn(
			Optional.of(notionUpload));

		// when
		RetrieveAiPostDetailResponse result = postService.retrievePostDetail(command);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getPostId()).isEqualTo(postId);
		assertThat(result.getPostTitle()).isEqualTo("제목");
		assertThat(result.getPostContent()).isEqualTo("본문");
		assertThat(result.getPostUrl()).isEqualTo("https://notion.so/my");
		assertThat(result.getTemplateId()).isEqualTo(111L);
		assertThat(result.getPromptId()).isEqualTo(222L);
		assertThat(result.getCreatedAt()).isNotNull();
		assertThat(result.getUpdatedAt()).isNotNull();
	}

	@Test
	@DisplayName("예외: 본인 글이 아닐 때 상세조회 불가")
	void retrievePostDetail_forbidden() {
		// given
		Long userId = 1L, postId = 2L;
		PostRetrieveDetailCommand command = new PostRetrieveDetailCommand(userId, postId);

		UserEntity user = mock(UserEntity.class);
		when(user.getId()).thenReturn(userId);
		when(userHelper.getUser(userId)).thenReturn(user);

		UserEntity anotherUser = mock(UserEntity.class);
		when(anotherUser.getId()).thenReturn(999L);

		PromptEntity prompt = mock(PromptEntity.class);
		TemplateEntity template = mock(TemplateEntity.class);
		when(prompt.getTemplate()).thenReturn(template);
		when(prompt.getId()).thenReturn(1L);

		PostEntity post = mock(PostEntity.class);
		when(post.getId()).thenReturn(postId);
		when(post.getUser()).thenReturn(anotherUser);
		when(post.getPrompt()).thenReturn(prompt);

		when(postRepository.findByIdWithPromptAndTemplate(postId)).thenReturn(Optional.of(post));

		// when & then
		ForbiddenException thrown = catchThrowableOfType(
			() -> postService.retrievePostDetail(command),
			ForbiddenException.class
		);
		assertThat(thrown).isNotNull();
		assertThat(thrown.getCode()).isEqualTo(ExceptionCode.POST_NOT_VALID.getCode());
	}

	@Test
	@DisplayName("예외: 게시글이 존재하지 않음")
	void retrievePostDetail_postNotFound() {
		// given
		Long userId = 3L, postId = 4L;
		PostRetrieveDetailCommand command = new PostRetrieveDetailCommand(userId, postId);

		UserEntity user = mock(UserEntity.class);
		when(user.getId()).thenReturn(userId);
		when(userHelper.getUser(userId)).thenReturn(user);

		when(postRepository.findByIdWithPromptAndTemplate(postId)).thenReturn(Optional.empty());

		// when & then
		BadRequestException thrown = catchThrowableOfType(
			() -> postService.retrievePostDetail(command),
			BadRequestException.class
		);
		assertThat(thrown).isNotNull();
		assertThat(thrown.getCode()).isEqualTo(ExceptionCode.POST_NOT_FOUND.getCode());
	}

	@Test
	@DisplayName("예외: 노션 업로드 엔티티 없음")
	void retrievePostDetail_notionUploadNotFound() {
		// given
		Long userId = 5L, postId = 10L;
		PostRetrieveDetailCommand command = new PostRetrieveDetailCommand(userId, postId);

		UserEntity user = mock(UserEntity.class);
		when(user.getId()).thenReturn(userId);
		when(userHelper.getUser(userId)).thenReturn(user);

		UserEntity postUser = mock(UserEntity.class);
		when(postUser.getId()).thenReturn(userId);

		PromptEntity prompt = mock(PromptEntity.class);
		TemplateEntity template = mock(TemplateEntity.class);
		when(prompt.getTemplate()).thenReturn(template);
		when(prompt.getId()).thenReturn(1L);

		PostEntity post = mock(PostEntity.class);
		when(post.getId()).thenReturn(postId);
		when(post.getUser()).thenReturn(postUser);
		when(post.getPrompt()).thenReturn(prompt);
		when(post.getCreatedAt()).thenReturn(Instant.now());
		when(post.getUpdatedAt()).thenReturn(Instant.now());

		when(postRepository.findByIdWithPromptAndTemplate(postId)).thenReturn(Optional.of(post));
		when(notionUploadRepository.findFirstByPostIdOrderByCreatedAtDesc(postId)).thenReturn(Optional.empty());

		RetrieveAiPostDetailResponse response = postService.retrievePostDetail(command);

		assertThat(response).isNotNull();
		assertThat(response.getPostUrl()).isNull(); // notionUpload 없으므로

	}

}