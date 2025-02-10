package com.aivle.project.controller;

import com.aivle.project.entity.HistoryEntity;
import com.aivle.project.service.OpportunitiesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class CalendarController {
    private final OpportunitiesService opportunitiesService;

    @GetMapping("/calendar")
    public String calendarRead(@ModelAttribute("id") String employeeId, Model model) {
        // 세션에서 employeeId 가져오기
        if (employeeId == null) {
            throw new IllegalArgumentException("Invalid employeeId: " + employeeId);
        }

        // employeeId를 사용하여 전체 히스토리 가져오기
        List<HistoryEntity> history = opportunitiesService.getHistoriesByEmployeeId(employeeId);
        model.addAttribute("history", history);

        return "calendar/calendar_read";
    }



}
