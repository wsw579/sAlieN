package com.aivle.project.repository;

import com.aivle.project.entity.AccountEntity;
import com.aivle.project.entity.ChatbotLogsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatbotLogsRepository extends JpaRepository<ChatbotLogsEntity, Long> {
    List<ChatbotLogsEntity> findAllByOrderByLogsIdDesc(); // logsId 기준 내림차순

}

