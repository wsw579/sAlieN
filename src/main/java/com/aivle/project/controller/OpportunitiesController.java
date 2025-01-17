package com.aivle.project.controller;

import com.aivle.project.dto.HistoryDto;
import com.aivle.project.dto.OpportunitiesDto;
import com.aivle.project.entity.HistoryEntity;
import com.aivle.project.entity.LeadsEntity;
import com.aivle.project.entity.OpportunitiesCommentEntity;
import com.aivle.project.entity.OpportunitiesEntity;
import com.aivle.project.repository.OpportunitiesRepository;
import com.aivle.project.service.OpportunitiesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class OpportunitiesController {
    private final OpportunitiesService opportunitiesService;
    private final OpportunitiesRepository opportunitiesRepository;


    // Read page
    @GetMapping("/opportunities")
    public String opportunities(Model model) {
        List<OpportunitiesEntity> opportunities = opportunitiesService.readOpportunities();

        // 데이터가 null이면 빈 리스트로 초기화
        if (opportunities == null) {
            opportunities = new ArrayList<>();
        }

        model.addAttribute("opportunities", opportunities);
        return "opportunities/opportunities_read";
    }

    // Detail page
    @GetMapping("/opportunities/detail/{opportunityId}")
    public String opportunities(@PathVariable Long opportunityId, Model model) {
        OpportunitiesEntity opportunities = opportunitiesService.searchOpportunities(opportunityId);
        // 히스토리 부분 수정
        List<HistoryEntity> history = opportunitiesService.getHistoryByOpportunityId(opportunityId);
        List<OpportunitiesCommentEntity> opportunitiesComments = opportunitiesService.getCommentsByOpportunityId(opportunityId);

        // 디버깅을 위해 로그 출력
        System.out.println("Opportunities: " + opportunities);
        opportunitiesComments.forEach(comment -> System.out.println("Comment: " + comment.getContent() + ", Date: " + comment.getCommentCreatedDate()));

        model.addAttribute("opportunities", opportunities);
        // 히스토리 수정
        model.addAttribute("history", history);
        model.addAttribute("opportunitiesComments", opportunitiesComments);
        return "opportunities/opportunities_detail";
    }


    // History Detail page
    @GetMapping("/opportunities/detail/{opportunityId}/history/{historyId}")
    public String opportunity_history(@PathVariable Long historyId, @PathVariable Long opportunityId, Model model) {
        HistoryEntity opportunity_history = opportunitiesService.searchHistory(historyId);
        model.addAttribute("history", opportunity_history);
        model.addAttribute("opportunityId", opportunityId);
        return "opportunities/opportunities_history_detail";
    }


    // create comment
    @PostMapping("/opportunities/detail/createcomment")
    public String createComment(@RequestParam("content") String content, @RequestParam("opportunityId") Long opportunityId) {
        opportunitiesService.createComment(content, opportunityId, "작성자"); // 작성자 이름을 실제로 설정
        return "redirect:/opportunities/detail/" + opportunityId + "#commentSection";
    }



    // Create model page (초기값으로 페이지 생성)
    @GetMapping("/opportunities/detail/create")
    public String opportunitiesCreate(Model model) {

        OpportunitiesEntity opportunities = new OpportunitiesEntity();

        opportunities.setOpportunityName("");
        opportunities.setRegion("");
        opportunities.setCompanySize(0);
        opportunities.setOpportunityInquiries("");
        opportunities.setCustomerEmployee("");
        opportunities.setQuantity(0);
        opportunities.setExpectedRevenue(0);
        opportunities.setCompanyRevenue(0);
        opportunities.setOpportunityNotes("");
        opportunities.setCreatedDate(LocalDate.now());
        opportunities.setTargetCloseDate(LocalDate.now());
        opportunities.setOpportunityStatus("");
        opportunities.setSuccessRate("");

        model.addAttribute("opportunities", opportunities);

        return "opportunities/opportunities_detail";
    }

    @PostMapping("/opportunities/detail/create")
    public String opportunitiesCreateNew(@ModelAttribute OpportunitiesDto opportunitiesDto) {

        opportunitiesService.createOpportunities(opportunitiesDto);

        return "redirect:/opportunities";
    }



    // create history 초기 페이지
    @GetMapping("/opportunities/detail/{opportunityId}/history/create")
    public String historyCreate(@PathVariable Long opportunityId, Model model) {

        HistoryEntity history = new HistoryEntity();
        OpportunitiesEntity opportunity = opportunitiesRepository.findById(opportunityId)
                .orElseThrow(()->new IllegalArgumentException("error"));

        history.setHistoryTitle("");
        history.setCustomerRepresentative("");
        history.setHistoryDate(LocalDate.now());
        history.setHistoryTime(LocalTime.now());
        history.setMeetingPlace("");
        history.setActionTaken("");
        history.setCompanySize("");
        history.setCustomerRequirements("");
        history.setOpportunity(new OpportunitiesEntity());

        model.addAttribute("history", history);
        model.addAttribute("opportunityId", opportunityId);

        return "opportunities/opportunities_history_detail";
    }

    @PostMapping("/opportunities/detail/{opportunityId}/history/create")
    public String createHistory(@PathVariable Long opportunityId, @ModelAttribute HistoryDto historyDto) {
        // opportunityId로 OpportunitiesEntity 조회
        OpportunitiesEntity opportunity = opportunitiesRepository.findById(opportunityId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid opportunity ID"));

        // HistoryDto에 OpportunitiesEntity를 설정
        historyDto.setOpportunityId(opportunity);  // DTO에 직접 OpportunitiesEntity를 설정

        // 서비스에서 데이터를 저장
        opportunitiesService.createHistory(historyDto);

        return "redirect:/opportunities/detail/" + opportunityId;
    }


    // Update history in detail page
    @PostMapping("/opportunities/detail/{opportunityId}/history/{historyId}/update")
    public String historyUpdate(@PathVariable("historyId") Long historyId, @ModelAttribute HistoryDto historyDto) {
        opportunitiesService.updateHistory(historyId, historyDto);
        return "redirect:/opportunities/detail/{opportunityId}/history/" + historyId;
    }

    @GetMapping("/opportunities/detail/{opportunityId}/history/{historyId}/delete")
    public String historyDeleteDetail(@PathVariable("historyId") Long historyId) {
        opportunitiesService.deleteHistory(historyId);

        return "redirect:/opportunities/detail/{opportunityId}";
    }


    // Update detail page
    @PostMapping("/opportunities/detail/{opportunityId}/update")
    public String opportunitiesUpdate(@PathVariable("opportunityId") Long opportunityId, @ModelAttribute OpportunitiesDto opportunitiesDto) {
        opportunitiesService.updateOpportunities(opportunityId, opportunitiesDto);
        return "redirect:/opportunities/detail/" + opportunityId;
    }

    // Delete detail page
    @GetMapping("/opportunities/detail/{opportunityId}/delete")
    public String opportunitiesDeleteDetail(@PathVariable("opportunityId") Long opportunityId) {
        opportunitiesService.deleteOpportunities(opportunityId);

        return "redirect:/opportunities";
    }

    // Delete read page (list)
    @PostMapping("/opportunities/detail/delete")
    public ResponseEntity<Void> deleteOpportunities(@RequestBody Map<String, List<Long>> request) {
        List<Long> ids = request.get("ids");
        System.out.println("deleteOpportunities Received IDs: " + ids); // 로그 추가
        opportunitiesService.deleteOpportunitiesByIds(ids);
        return ResponseEntity.ok().build(); // 상태 코드 200 반환
    }


}

