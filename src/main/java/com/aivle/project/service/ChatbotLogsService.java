package com.aivle.project.service;

import com.aivle.project.entity.ChatbotLogsEntity;
import com.aivle.project.repository.ChatbotLogsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatbotLogsService {

    private final ChatbotLogsRepository chatbotLogsRepository;

    // Read
    public Page<ChatbotLogsEntity> readChatbotLogs(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size);
        return chatbotLogsRepository.findAllByKeyword(search, pageable); // 내림차순으로 로그 가져오기
    }
}

