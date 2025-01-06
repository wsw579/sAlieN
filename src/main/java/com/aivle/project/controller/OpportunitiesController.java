package com.aivle.project.controller;


import com.aivle.project.dto.OpportunitiesDto;
import com.aivle.project.entity.OpportunitiesEntity;
import com.aivle.project.service.OpportunitiesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class OpportunitiesController {
    private final OpportunitiesService opportunitiesService;


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
        model.addAttribute("opportunities", opportunities);

        return "opportunities/opportunities_detail";
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

