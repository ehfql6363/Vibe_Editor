package com.ssafy.vibe.notion.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ssafy.vibe.notion.domain.NotionDatabaseEntity;

public interface NotionDatabaseRepository extends JpaRepository<NotionDatabaseEntity, Long> {
	List<NotionDatabaseEntity> findAllByUserIdOrderByUpdatedAtDesc(Long userId);

	Optional<NotionDatabaseEntity> findByDatabaseUidAndUserId(String databaseUid, Long userId);

	Optional<NotionDatabaseEntity> findById(Long notionDatabaseId);
}
