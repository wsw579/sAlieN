package com.aivle.project.controller;

import com.aivle.project.entity.ChatbotLogsEntity;
import com.aivle.project.entity.CrudLogsEntity;
import com.aivle.project.entity.HistoryEntity;
import com.aivle.project.service.ChatbotLogsService;
import com.aivle.project.service.CrudLogsService;
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
    private final CrudLogsService crudLogsService;

    // 알람 표시
    @GetMapping("/alarm")
    public String alarmLogs(@ModelAttribute("id") String employeeId, Model model) {
        // 세션에서 employeeId 가져오기
        if (employeeId == null) {
            throw new IllegalArgumentException("Invalid employeeId: " + employeeId);
        }

        return "logs/alarm"; // 뷰 파일로 이동
    }


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

        // 로그 데이터를 조회하여 모델에 추가
        List<CrudLogsEntity> logs = crudLogsService.readCrudLogs();
        model.addAttribute("logs", logs);

        return "logs/crud_logs";
    }


}
