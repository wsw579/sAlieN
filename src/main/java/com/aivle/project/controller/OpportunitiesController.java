package com.aivle.project.controller;


import com.aivle.project.entity.OpportunitiesEntity;
import com.aivle.project.service.OpportunitiesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

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







}

