package com.ssafy.vibe.template.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ssafy.vibe.template.domain.TemplateEntity;

@Repository
public interface TemplateRepository extends JpaRepository<TemplateEntity, Long> {
	@Query("""
		select te
		from TemplateEntity te
		where
			te.user.id = :userId
			and te.isDeleted = false
		""")
	List<TemplateEntity> findByUserIdAndActive(@Param("userId") Long userId);

	@Query("""
		select te
		from TemplateEntity te
		where
			te.user.id = :userId
			and te.id = :templateId
			and te.isDeleted = false
		order by te.updatedAt desc
		""")
	Optional<TemplateEntity> findByIdAndActive(
		@Param("userId") Long userId,
		@Param("templateId") Long templateId
	);

	Optional<TemplateEntity> findById(Long templateId);
}
