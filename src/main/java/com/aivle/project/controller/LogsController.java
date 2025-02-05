package com.aivle.project.controller;

import com.aivle.project.dto.PaginationDto;
import com.aivle.project.entity.ChatbotLogsEntity;
import com.aivle.project.entity.ContractsEntity;
import com.aivle.project.entity.CrudLogsEntity;
import com.aivle.project.entity.HistoryEntity;
import com.aivle.project.service.ChatbotLogsService;
import com.aivle.project.service.CrudLogsService;
import com.aivle.project.service.PaginationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class LogsController {

    private final ChatbotLogsService chatbotLogsService;
    private final CrudLogsService crudLogsService;
    private final PaginationService paginationService;

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
    public String crudLogs(@RequestParam Map<String, String> params,
                           @ModelAttribute("id") String employeeId,
                           Model model) {
        int page = Integer.parseInt(params.getOrDefault("page", "0"));
        int size = Integer.parseInt(params.getOrDefault("size", "20"));
        String search = params.getOrDefault("search", "");

        // 데이터 조회
        Page<CrudLogsEntity> logsPage = crudLogsService.readCrudLogs(page, size, search);

        // 페이지네이션 데이터 생성
        PaginationDto<CrudLogsEntity> paginationDto = paginationService.createPaginationData(logsPage, page, 5);


        // 세션에서 employeeId 가져오기
        if (employeeId == null) {
            throw new IllegalArgumentException("Invalid employeeId: " + employeeId);
        }

        // Model에 데이터 추가
        model.addAttribute("pagination", paginationDto);

        // 검색 및 정렬 데이터
        model.addAttribute("search", search); // 검색어

        return "logs/crud_logs";
    }


}
