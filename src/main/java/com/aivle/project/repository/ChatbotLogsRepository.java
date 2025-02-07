package com.aivle.project.repository;

import com.aivle.project.entity.ChatbotLogsEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatbotLogsRepository extends JpaRepository<ChatbotLogsEntity, Long> {
    // ✅ 특정 키워드를 포함하는 로그 검색 + logsId 기준 내림차순 정렬 + 페이징 지원
    @Query("SELECT c FROM ChatbotLogsEntity c WHERE c.userId LIKE %:keyword% ORDER BY c.logsId DESC")
    Page<ChatbotLogsEntity> findAllByKeyword(@Param("keyword") String keyword, Pageable pageable);

}

