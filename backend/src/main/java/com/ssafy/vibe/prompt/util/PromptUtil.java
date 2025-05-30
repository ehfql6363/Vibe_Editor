package com.ssafy.vibe.prompt.util;

import static com.ssafy.vibe.common.exception.ExceptionCode.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.ssafy.vibe.common.exception.NotFoundException;
import com.ssafy.vibe.prompt.domain.OptionEntity;
import com.ssafy.vibe.prompt.domain.PromptAttachEntity;
import com.ssafy.vibe.prompt.domain.PromptEntity;
import com.ssafy.vibe.prompt.domain.PromptOptionEntity;
import com.ssafy.vibe.prompt.repository.OptionRepository;
import com.ssafy.vibe.prompt.repository.PromptRepository;
import com.ssafy.vibe.prompt.service.command.SnapshotCommand;
import com.ssafy.vibe.prompt.service.dto.PromptAttachDTO;
import com.ssafy.vibe.prompt.service.dto.PromptOptionDTO;
import com.ssafy.vibe.prompt.template.PromptTemplate;
import com.ssafy.vibe.snapshot.domain.SnapshotEntity;
import com.ssafy.vibe.snapshot.repository.SnapshotRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class PromptUtil {
	private final PromptTemplate promptTemplate;
	private final PromptRepository promptRepository;
	private final SnapshotRepository snapshotRepository;
	private final OptionRepository optionRepository;

	public String buildUserPromptContent(PromptEntity prompt) {
		String snapshotsFormatted = formatSnapshots(
			prompt.getAttachments().stream()
				.filter((pr) -> !pr.getIsDeleted())
				.toList());
		String optionsFormatted = formatOptions(
			prompt.getPromptOptions().stream()
				.filter((pr) -> !pr.getIsDeleted())
				.toList());

		String BLOG_PROMPT_TEMPLATE = promptTemplate.getPromptTemplate();

		return String.format(BLOG_PROMPT_TEMPLATE,
			prompt.getPostType(),
			snapshotsFormatted,
			prompt.getComment(),
			optionsFormatted
		);
	}

	public List<PromptAttachEntity> buildPromptAttachments(
		Long promptId,
		List<SnapshotCommand> snapshotCommandList) {
		List<PromptAttachDTO> promptAttachDTOList = snapshotCommandList.stream()
			.map(snapshotCommand -> {
				SnapshotEntity snapshot = snapshotRepository.findById(snapshotCommand.getSnapshotId())
					.orElseThrow(() -> new NotFoundException(SNAPSHOT_NOT_FOUND));
				return snapshotCommand.toDTO(promptId, snapshot.getId());
			}).toList();

		return promptAttachDTOList.stream()
			.map(promptAttachDTO -> {
				PromptEntity prompt = promptRepository.findById(promptAttachDTO.getPromptId())
					.orElseThrow(() -> new NotFoundException(PROMPT_NOT_FOUND));
				SnapshotEntity snapshot = snapshotRepository.findById(promptAttachDTO.getSnapshotId())
					.orElseThrow(() -> new NotFoundException(SNAPSHOT_NOT_FOUND));
				return promptAttachDTO.toEntity(prompt, snapshot);
			})
			.toList();
	}

	public List<PromptOptionEntity> buildPromptOptions(
		Long promptId,
		List<Long> promptOptionIds) {
		List<PromptOptionDTO> promptOptionDTOList = promptOptionIds.stream()
			.map(optionId -> PromptOptionDTO.from(promptId, optionId))
			.toList();

		return promptOptionDTOList.stream()
			.map(promptOptionDTO -> {
				PromptEntity prompt = promptRepository.findById(promptOptionDTO.getPromptId())
					.orElseThrow(() -> new NotFoundException(PROMPT_NOT_FOUND));
				OptionEntity option = optionRepository.findById(promptOptionDTO.getOptionId())
					.orElseThrow(() -> new NotFoundException(OPTION_NOT_FOUND));

				return PromptOptionDTO.toEntity(prompt, option);
			}).toList();
	}

	private String formatSnapshots(List<PromptAttachEntity> attachments) {
		String snapshotsFormatted = attachments.stream()
			.map(promptAttachEntity ->
				snapshotRepository.findById(promptAttachEntity.getSnapshot().getId())
					.map(
						snapshot -> String.format(
							"""
								    * snapshot :
								    ```
								    %s
								    ```
								
								    * description :
								    ```
								    %s
								    ```
								""",
							snapshot.getSnapshotContent(),
							promptAttachEntity.getDescription()))
					.orElseThrow(() -> new NotFoundException(SNAPSHOT_NOT_FOUND)))
			.collect(Collectors.joining("\n"));

		if (snapshotsFormatted.isEmpty()) {
			snapshotsFormatted = "입력된 코드 스냅샷-스냅샷에 대한 설명 쌍이 없습니다. 다른 정보들을 기반으로 포스트 초안을 만듭니다.";
		}

		return snapshotsFormatted;
	}

	private String formatOptions(List<PromptOptionEntity> promptOptions) {
		StringBuilder optionsFormatted = new StringBuilder();
		for (PromptOptionEntity promptOption : promptOptions) {
			Optional<OptionEntity> option = optionRepository.findById(promptOption.getOption().getId());
			option.ifPresent(optionEntity -> {
				optionsFormatted.append(option.get().getOptionName())
					.append(" : ")
					.append(option.get().getValue())
					.append("\n");
			});
		}

		if (optionsFormatted.isEmpty()) {
			optionsFormatted.append("이모지 포함 및 문체 스타일은 알아서 합니다.");
		}

		return optionsFormatted.toString();
	}
}
