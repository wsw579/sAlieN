package com.aivle.project.repository;

import com.aivle.project.entity.AccountEntity;
import com.aivle.project.entity.ChatbotLogsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatbotLogsRepository extends JpaRepository<ChatbotLogsEntity, Long> {


}

