package com.ssafy.vibe.post.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ssafy.vibe.post.domain.PostEntity;

public interface PostRepository extends JpaRepository<PostEntity, Long> {

	@Query("""
		select p
		from PostEntity p
			join fetch p.prompt pr
			join fetch pr.notionDatabase nd
		where p.id = :id
			and p.isDeleted = false
		""")
	Optional<PostEntity> findByIdWithPromptAndNotionDatabase(@Param("id") Long id);

	@Query("""
		select p
		from PostEntity p
			join fetch p.user pu
		where pu.id = :user_id
			and p.isDeleted = false
		""")
	List<PostEntity> findAllByUserId(@Param("user_id") Long id);

	@Query("""
		select p
		from PostEntity p
			join fetch p.prompt pr
			join fetch pr.template t
			join fetch p.user u
			join fetch p.userAiProvider uap
			join fetch uap.aiProvider ap
		where p.id = :id
			and p.isDeleted = false
		""")
	Optional<PostEntity> findByIdWithPromptAndTemplate(@Param("id") Long id);
}
