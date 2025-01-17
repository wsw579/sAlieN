package com.aivle.project.controller;

import com.aivle.project.dto.AdditionalOpportunitiesDto;
import com.aivle.project.entity.*;
import com.aivle.project.repository.OpportunitiesRepository;
import com.aivle.project.repository.ProductsRepository;
import com.aivle.project.repository.AccountRepository;
import com.aivle.project.service.AdditionalOpportunitiesService;
import com.aivle.project.service.LeadsService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
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

public class AdditionalOpportunitiesController {
    private final AdditionalOpportunitiesService additionalopportunitiesService;
    private final OpportunitiesRepository opportunitiesRepository;
    private final AccountRepository accountRepository;
    private final ProductsRepository productsRepository;

    //read page
    @GetMapping("/additionalopportunities")
    public String additionalopportunities(Model model) {
        List<AdditionalOpportunitiesEntity> additionalopportunities = additionalopportunitiesService.readAdditionalOpportunities();
        if (additionalopportunities == null) {
            additionalopportunities = new ArrayList<>();
        }
        model.addAttribute("additionalopportunities", additionalopportunities);
        return "additionalopportunities/additionalopportunities_read";
    }
    //detail page
    @GetMapping("/additionalopportunities/detail/{additionalopportunityId}")
    public String additionalopportunities(@PathVariable Long additionalopportunityId, Model model){
        AdditionalOpportunitiesEntity additionalopportunities = additionalopportunitiesService.searchAdditionalOpportunities(additionalopportunityId);
        List<OpportunitiesEntity> opportunities =  opportunitiesRepository.findAll();
        List<ProductsEntity> products = productsRepository.findAll();
        List<AccountEntity> accounts = accountRepository.findAll();

        System.out.println("AdditionalOpportunities: "+ additionalopportunities);

        model.addAttribute("AdditionalOpportunities", additionalopportunities);
        model.addAttribute("opportunities", opportunities);
        model.addAttribute("products", products);
        model.addAttribute("accounts", accounts);

        return "additionalopportunities/additionalopportunities_detail";
    }
    //new additional opportunities
    //@GetMapping("additionalopportunities/detail/create")
    //public String additionalopportunitiesCreate(Model model){
        //AdditionalOpportunitiesEntity additionalopportunities = new AdditionalOpportunitiesEntity();
    //}

}
