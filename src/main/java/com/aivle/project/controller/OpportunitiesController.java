package com.aivle.project.controller;

import com.aivle.project.dto.OpportunitiesDto;
import com.aivle.project.entity.*;
import com.aivle.project.repository.AccountRepository;
import com.aivle.project.repository.EmployeeRepository;
import com.aivle.project.repository.LeadsRepository;
import com.aivle.project.repository.ProductsRepository;
import com.aivle.project.service.OpportunitiesService;
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
public class OpportunitiesController {
    private final OpportunitiesService opportunitiesService;
    private final ProductsRepository productsRepository;
    private final AccountRepository accountRepository;
    private final EmployeeRepository employeeRepository;
    private final LeadsRepository leadsRepository;


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
        List<OpportunitiesCommentEntity> opportunitiesComments = opportunitiesService.getCommentsByOpportunityId(opportunityId);

        // 목록 조회 후 모델에 추가 (드롭다운 메뉴용)
        List<ProductsEntity> products = productsRepository.findAll();
        List<AccountEntity> accounts = accountRepository.findAll();
        List<EmployeeEntity> employee = employeeRepository.findAll();
        List<LeadsEntity> leads = leadsRepository.findAll();


        // 디버깅을 위해 로그 출력
        System.out.println("Opportunities: " + opportunities);
        opportunitiesComments.forEach(comment -> System.out.println("Comment: " + comment.getContent() + ", Date: " + comment.getCommentCreatedDate()));

        model.addAttribute("opportunities", opportunities);
        model.addAttribute("opportunitiesComments", opportunitiesComments);
        model.addAttribute("products", products);
        model.addAttribute("accounts", accounts);
        model.addAttribute("employee", employee);
        model.addAttribute("leads", leads);
        return "opportunities/opportunities_detail";
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

        // 목록 조회 후 모델에 추가 (드롭다운 메뉴용)
        List<ProductsEntity> products = productsRepository.findAll();
        List<AccountEntity> accounts = accountRepository.findAll();
        List<EmployeeEntity> employee = employeeRepository.findAll();
        List<LeadsEntity> leads = leadsRepository.findAll();


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

        //외래키 부분
        opportunities.setLeadId(new LeadsEntity());
        opportunities.setAccountId(new AccountEntity());
        opportunities.setProductId(new ProductsEntity());
        opportunities.setEmployeeId(new EmployeeEntity());

        model.addAttribute("opportunities", opportunities);
        model.addAttribute("products", products);
        model.addAttribute("accounts", accounts);
        model.addAttribute("employee", employee);
        model.addAttribute("leads", leads);
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

