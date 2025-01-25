package com.aivle.project.controller;

import com.aivle.project.entity.ChatbotLogsEntity;
import com.aivle.project.entity.HistoryEntity;
import com.aivle.project.service.ChatbotLogsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class LogsController {

    private final ChatbotLogsService chatbotLogsService;


    @GetMapping("/chatbot_logs")
    public String chatbotLogs(@ModelAttribute("id") String employeeId, Model model) {
        // 세션에서 employeeId 가져오기
        if (employeeId == null) {
            throw new IllegalArgumentException("Invalid employeeId: " + employeeId);
        }

        // 챗봇 로그 데이터를 조회하여 모델에 추가
        List<ChatbotLogsEntity> logs = chatbotLogsService.readChatbotLogs();
        model.addAttribute("logs", logs);

        return "logs/chatbot_logs"; // 뷰 파일로 이동
    }


    @GetMapping("/crud_logs")
    public String crudLogs(@ModelAttribute("id") String employeeId, Model model) {
        // 세션에서 employeeId 가져오기
        if (employeeId == null) {
            throw new IllegalArgumentException("Invalid employeeId: " + employeeId);
        }

        return "logs/crud_logs";
    }


}
