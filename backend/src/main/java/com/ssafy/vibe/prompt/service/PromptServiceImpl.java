package com.ssafy.vibe.prompt.service;

import static com.ssafy.vibe.common.exception.ExceptionCode.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.vibe.common.domain.BaseEntity;
import com.ssafy.vibe.common.exception.BadRequestException;
import com.ssafy.vibe.common.exception.NotFoundException;
import com.ssafy.vibe.common.util.Aes256Util;
import com.ssafy.vibe.notion.domain.NotionDatabaseEntity;
import com.ssafy.vibe.notion.repository.NotionDatabaseRepository;
import com.ssafy.vibe.post.domain.PostEntity;
import com.ssafy.vibe.post.domain.PostType;
import com.ssafy.vibe.post.repository.PostRepository;
import com.ssafy.vibe.post.service.dto.PostSaveDTO;
import com.ssafy.vibe.prompt.controller.response.CreatedPostResponse;
import com.ssafy.vibe.prompt.controller.response.OptionResponse;
import com.ssafy.vibe.prompt.controller.response.PromptAttachRespnose;
import com.ssafy.vibe.prompt.controller.response.RetrievePromptResponse;
import com.ssafy.vibe.prompt.domain.PromptAttachEntity;
import com.ssafy.vibe.prompt.domain.PromptEntity;
import com.ssafy.vibe.prompt.domain.PromptOptionEntity;
import com.ssafy.vibe.prompt.repository.OptionRepository;
import com.ssafy.vibe.prompt.repository.PromptAttachRepository;
import com.ssafy.vibe.prompt.repository.PromptOptionRepository;
import com.ssafy.vibe.prompt.repository.PromptRepository;
import com.ssafy.vibe.prompt.service.command.GeneratePostCommand;
import com.ssafy.vibe.prompt.service.command.PromptAttachUpdateCommand;
import com.ssafy.vibe.prompt.service.command.PromptSaveCommand;
import com.ssafy.vibe.prompt.service.command.PromptUpdateCommand;
import com.ssafy.vibe.prompt.service.dto.AiChatInputDTO;
import com.ssafy.vibe.prompt.service.dto.OptionItemDTO;
import com.ssafy.vibe.prompt.service.dto.PromptAttachDTO;
import com.ssafy.vibe.prompt.service.dto.PromptSaveDTO;
import com.ssafy.vibe.prompt.service.dto.RetrievePromptAttachDTO;
import com.ssafy.vibe.prompt.service.dto.RetrievePromptDTO;
import com.ssafy.vibe.prompt.template.PromptTemplate;
import com.ssafy.vibe.prompt.util.PromptUtil;
import com.ssafy.vibe.snapshot.domain.SnapshotEntity;
import com.ssafy.vibe.snapshot.repository.SnapshotRepository;
import com.ssafy.vibe.template.domain.TemplateEntity;
import com.ssafy.vibe.template.repository.TemplateRepository;
import com.ssafy.vibe.user.domain.AiProviderEntity;
import com.ssafy.vibe.user.domain.UserAiProviderEntity;
import com.ssafy.vibe.user.domain.UserEntity;
import com.ssafy.vibe.user.repository.UserAiProviderRepository;
import com.ssafy.vibe.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PromptServiceImpl implements PromptService {
	private static final ObjectMapper mapper = new ObjectMapper();

	private final PromptTemplate promptTemplate;
	private final UserRepository userRepository;
	private final SnapshotRepository snapshotRepository;
	private final OptionRepository optionRepository;
	private final TemplateRepository templateRepository;
	private final PromptRepository promptRepository;
	private final PromptAttachRepository promptAttachRepository;
	private final PromptOptionRepository promptOptionRepository;
	private final NotionDatabaseRepository notionDatabaseRepository;
	private final PostRepository postRepository;
	private final UserAiProviderRepository userAiProviderRepository;
	private final PromptUtil promptUtil;
	private final AiChatServiceFactory aiChatServiceFactory;
	private final Aes256Util aes256Util;

	@Override
	public CreatedPostResponse createDraft(Long userId, GeneratePostCommand generatePostCommand) {
		PromptEntity prompt = promptRepository.findById(generatePostCommand.getPromptId())
			.orElseThrow(() -> new NotFoundException(PROMPT_NOT_FOUND));

		checkPromptUser(userId, prompt.getUser().getId());

		if (prompt.getComment() == null) {
			throw new BadRequestException(PROMPT_CONTENT_NULL);
		}

		if (prompt.getUserAiProvider() == null) {
			throw new BadRequestException(USER_AI_PROVIDER_NULL);
		}

		String generatedUserPrompt = promptUtil.buildUserPromptContent(prompt);
		UserAiProviderEntity userAiProvider = prompt.getUserAiProvider();
		AiProviderEntity aiProvider = userAiProvider.getAiProvider();

		String apiKey = Optional.ofNullable(userAiProvider.getApiKey())
			.filter(key -> key != null && !key.isEmpty())
			.map(aes256Util::decrypt)
			.orElse(null);

		AiChatService aiChatService = aiChatServiceFactory.get(aiProvider.getBrand());
		String SYSTEM_PROMPT = promptTemplate.getSystemPrompt();

		AiChatInputDTO aiChatInputDTO = new AiChatInputDTO(
			aiProvider.getModel(), userAiProvider.getIsDefault(), userAiProvider.getTemperature(), apiKey,
			SYSTEM_PROMPT, generatedUserPrompt);

		String[] parsedContentArray = aiChatService.generateChat(aiChatInputDTO);
		PostSaveDTO postDTO = PostSaveDTO.from(
			null,
			prompt,
			parsedContentArray[0],
			parsedContentArray[1]
		);

		PostEntity post = postDTO.toEntity();
		post = postRepository.save(post);

		return CreatedPostResponse.from(
			post.getId(),
			post.getPostTitle(),
			post.getPostContent());
	}

	@Override
	public void savePrompt(Long userId, PromptSaveCommand promptSaveCommand) {
		UserEntity user = userRepository.findById(userId)
			.orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));

		PromptSaveDTO promptSaveDTO = new PromptSaveDTO(
			promptSaveCommand, user
		);

		PromptEntity parentPrompt = null;
		if (promptSaveDTO.getParentPromptId() != null) {
			parentPrompt = promptRepository.findById(promptSaveDTO.getParentPromptId())
				.orElse(null);
		}

		TemplateEntity template = templateRepository.findById(promptSaveDTO.getTemplateId())
			.orElseThrow(() -> new BadRequestException(TEMPLATE_NOT_FOUND));

		NotionDatabaseEntity notionDatabase = notionDatabaseRepository.findById(promptSaveDTO.getNotionDatabaseId())
			.orElseThrow(() -> new BadRequestException(NOTION_DATABASE_NOT_FOUND));

		UserAiProviderEntity userAIProvider = null;
		Long userAIProviderId = promptSaveDTO.getUserAIProviderId();
		if (userAIProviderId != null) {
			userAIProvider = userAiProviderRepository.findById(
					userAIProviderId)
				.orElseThrow(() -> new BadRequestException(USER_AI_PROVIDER_NOT_FOUND));
		}

		PromptEntity prompt = promptSaveDTO.toEntity(
			parentPrompt,
			template,
			user,
			notionDatabase,
			userAIProvider);
		prompt = promptRepository.save(prompt);

		List<PromptAttachEntity> promptAttachList = promptUtil.buildPromptAttachments(
			prompt.getId(),
			promptSaveCommand.getPromptAttachList());
		promptAttachRepository.saveAll(promptAttachList);
		prompt.getAttachments().addAll(promptAttachList);

		List<PromptOptionEntity> promptOptions = promptUtil.buildPromptOptions(
			prompt.getId(),
			promptSaveCommand.getPromptOptionList());
		promptOptionRepository.saveAll(promptOptions);
		prompt.getPromptOptions().addAll(promptOptions);

		promptRepository.save(prompt);
	}

	@Override
	public RetrievePromptResponse getPrompt(Long userId, Long promptId) {
		PromptEntity prompt = promptRepository.findById(promptId)
			.orElseThrow(() -> new NotFoundException(PROMPT_NOT_FOUND));

		checkPromptUser(userId, prompt.getUser().getId());

		RetrievePromptDTO retrievePromptDTO = RetrievePromptDTO.fromEntity(prompt);
		List<PromptAttachEntity> promptAttachEntityList = promptAttachRepository.findByPromptAndIsDeletedFalse(prompt);
		List<PromptOptionEntity> promptOptionEntityList = promptOptionRepository.findByPromptAndIsDeletedFalse(prompt);

		List<PromptAttachRespnose> promptAttachList = promptAttachEntityList.stream()
			.map(RetrievePromptAttachDTO::from)
			.map(PromptAttachRespnose::from)
			.toList();

		List<Long> promptOptionIds = promptOptionEntityList.stream()
			.map(promptOptionEntity -> promptOptionEntity.getOption().getId())
			.toList();

		PromptEntity parentPrompt = null;
		if (prompt.getParentPrompt() != null) {
			parentPrompt = promptRepository.findById(prompt.getParentPrompt().getId())
				.orElse(null);
		}

		RetrievePromptDTO retrieveParentPromptDTO = null;
		if (parentPrompt != null) {
			retrieveParentPromptDTO = RetrievePromptDTO.fromEntity(parentPrompt);
		}

		return RetrievePromptResponse.from(retrievePromptDTO, retrieveParentPromptDTO, promptAttachList,
			promptOptionIds);
	}

	@Override
	public void updatePrompt(Long userId, Long promptId, PromptUpdateCommand promptUpdateCommand) {
		PromptEntity prompt = promptRepository.findById(promptId)
			.orElseThrow(() -> new NotFoundException(PROMPT_NOT_FOUND));

		checkPromptUser(userId, prompt.getUser().getId());

		prompt.updatePromptName(promptUpdateCommand.getPromptName());
		prompt.updatePoostType(PostType.valueOf(promptUpdateCommand.getPostType()));
		prompt.updateComment(promptUpdateCommand.getComment());

		UserAiProviderEntity userAIProvider = null;
		Long userAIProviderId = promptUpdateCommand.getUserAIProviderId();
		if (userAIProviderId != null) {
			userAIProvider = userAiProviderRepository.findById(
					userAIProviderId)
				.orElseThrow(() -> new BadRequestException(USER_AI_PROVIDER_NOT_FOUND));
		}
		prompt.updateUserAIProvider(userAIProvider);

		List<Long> promptAttachEntitiyIdsToUpdate = new ArrayList<>();
		List<PromptAttachEntity> newPromptAttachEntitiesToInsert = new ArrayList<>();

		// PromptAttach 업데이트
		// 1. 새롭게 받은 PromptAttachList의 promptAttachId 조회
		for (PromptAttachUpdateCommand command : promptUpdateCommand.getPromptAttachList()) {
			if (command.getAttachId() != null) { // 기존에 저장됐었던 PromptAttachEntity인 경우
				PromptAttachEntity existingPromptAttach = promptAttachRepository.findById(command.getAttachId())
					.orElseThrow(() -> new NotFoundException(PROMPT_ATTACH_NOT_FOUND));
				// 바로 내용 수정
				existingPromptAttach.updateDescription(command.getDescription());

				// 기존에 저장돼 있었던 PromptAttachEntity 중 수정 요청에 포함되지 않은 객체에 대해 isDeleted=true로 업데이트하기 위해 id 저장
				promptAttachEntitiyIdsToUpdate.add(existingPromptAttach.getId());
			} else { // 새롭게 입력된 PromptAttachEntity인 경우
				SnapshotEntity snapshot = snapshotRepository.findById(command.getSnapshotId())
					.orElseThrow(() -> new NotFoundException(SNAPSHOT_NOT_FOUND));

				PromptAttachDTO promptAttachDTO = command.toDTO(prompt.getId(), snapshot.getId());
				PromptAttachEntity promptAttachEntity = promptAttachDTO.toEntity(prompt, snapshot);

				newPromptAttachEntitiesToInsert.add(promptAttachEntity);
			}
		}

		// 2. 기존 PromptAttachEntityList 조회
		List<PromptAttachEntity> allPromptAttachEntities = promptAttachRepository.findByPrompt(prompt);

		// 3. 기존에 있고, isDeleted=false인데 새롭게 들어온 PromptAttachList > promptAttachIds 에 없는 경우 isDeleted=true 로 업데이트
		for (PromptAttachEntity existingPromptAttachEntity : allPromptAttachEntities) {
			Long promptAttachId = existingPromptAttachEntity.getId();

			if (!promptAttachEntitiyIdsToUpdate.contains(promptAttachId)) {
				existingPromptAttachEntity.setIsDeleted(true);
			}
		}

		// 4. 새롭게 들어온 PromptAttach 저장
		promptAttachRepository.saveAll(newPromptAttachEntitiesToInsert);

		// PromptOption 업데이트
		List<PromptOptionEntity> existingPromptOptions = promptOptionRepository.findByPrompt(prompt);
		List<Long> newOptionIdList = promptUpdateCommand.getPromptOptionList();
		updatePromptOptions(existingPromptOptions, newOptionIdList, prompt);

		promptRepository.save(prompt);
	}

	@Override
	public List<OptionResponse> getOptionList() {
		Map<String, List<OptionItemDTO>> optionMap = new HashMap<>();
		optionRepository.findAll().forEach(option -> {
			String optionName = option.getOptionName().toString();
			optionName = optionName.equals("EMOJI") ? "이모지" : "문체";
			OptionItemDTO optionItem = OptionItemDTO.from(option);

			optionMap.computeIfAbsent(optionName, k -> new ArrayList<>()).add(optionItem);
		});

		return optionMap.entrySet().stream()
			.map(option ->
				OptionResponse.from(option.getKey(), option.getValue())).collect(Collectors.toList());
	}

	@Override
	public void deletePrompt(Long userId, Long promptId) {
		PromptEntity prompt = promptRepository.findById(promptId)
			.orElseThrow(() -> new NotFoundException(PROMPT_NOT_FOUND));

		checkPromptUser(userId, prompt.getUser().getId());

		prompt.updateIsDeleted();
		prompt.getAttachments().forEach(BaseEntity::updateIsDeleted);
		prompt.getPromptOptions().forEach(BaseEntity::updateIsDeleted);

		promptRepository.save(prompt);
		promptAttachRepository.saveAll(prompt.getAttachments());
		promptOptionRepository.saveAll(prompt.getPromptOptions());
	}

	private void updatePromptOptions(
		List<PromptOptionEntity> existingPromptOptions,
		List<Long> newOptionIdList,
		PromptEntity prompt) {
		existingPromptOptions.forEach(option -> option.setIsDeleted(true));

		existingPromptOptions.forEach(promptOption -> {
			Long existingOptionId = promptOption.getOption().getId();
			if (newOptionIdList.contains(existingOptionId)) {
				promptOption.setIsDeleted(false);
				newOptionIdList.remove(existingOptionId);
			}
		});

		List<PromptOptionEntity> newPromptOptions = promptUtil.buildPromptOptions(
			prompt.getId(),
			newOptionIdList);
		promptOptionRepository.saveAll(newPromptOptions);
	}

	private void checkPromptUser(Long userId, Long promptUserId) {
		if (!userId.equals(promptUserId)) {
			throw new BadRequestException(OWNER_MISMATCH);
		}
	}
}
