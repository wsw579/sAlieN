package com.aivle.project.controller;

import com.aivle.project.dto.LeadsDto;
import com.aivle.project.entity.*;
import com.aivle.project.repository.AccountRepository;
import com.aivle.project.repository.EmployeeRepository;
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
//automatically generates a constructor for all final fields in the class
@RequiredArgsConstructor
public class LeadsController {
    // declares a dependency on the LeadsService class
    private final LeadsService leadsService;
    private final AccountRepository accountRepository;
    private final EmployeeRepository employeeRepository;

    // Read Page
    @GetMapping("/leads")
    // model object pass data from the controller to the view
    // retrieves the leads list  -> add to the model -> displayed on the view
    public String leads(Model model) {
        // calls readLeads method from the leadsService
        // The result (a List<LeadsEntity>) is stored in the leads variable
        List<LeadsEntity> leads = leadsService.readLeads();

        // make an empty ArrayList to prevent any null pointer exceptions
        if (leads == null) {
            leads = new ArrayList<>();
        }


        // key-value pair to the Model object
        // "leads" : the name to be referenced in the view
        // leads : LeadsEntity objects (leads list)
        model.addAttribute("leads", leads);
        return "leads/leads_read";}




    //Detail page
    @GetMapping("/leads/detail/{leadId}")
    public String leads(@PathVariable Long leadId, Model model){
        LeadsEntity leads = leadsService.searchLeads(leadId);

        List<AccountEntity> accounts = accountRepository.findAll();
        List<EmployeeEntity> employee = employeeRepository.findAll();


        System.out.println("Leads: "+ leads);


        model.addAttribute("leads", leads);
        model.addAttribute("accounts", accounts);
        model.addAttribute("employee", employee);
        return "leads/leads_detail";
    }

    // Create model page
    // new lead를 만드는 페이지
    @GetMapping("/leads/detail/create")
    public String leadsCreate(Model model) {
        // new instance of the LeadsEntity class
        LeadsEntity leads = new LeadsEntity();

        List<AccountEntity> accounts = accountRepository.findAll();
        List<EmployeeEntity> employee = employeeRepository.findAll();

        leads.setLeadStatus("");
        leads.setLeadSource("");
        leads.setCreatedDate(LocalDate.now());
        leads.setTargetCloseDate(LocalDate.now());
        leads.setCustomerRequirements("");
        leads.setCustomerRepresentitive("");
        leads.setC_tel("");

        //외래키
        leads.setAccountId(new AccountEntity());
        leads.setEmployeeId(new EmployeeEntity());


        // leads_detail.html 에 "leads"가 보일 수 있도록
        model.addAttribute("leads", leads);
        model.addAttribute("accounts", accounts);
        model.addAttribute("employee", employee);

        return "leads/leads_detail";
    }

    @PostMapping("/leads/detail/create")
    public String leadsCreateNew(@ModelAttribute LeadsDto leadsDto){
        // createLeads method in the LeadsService -> passing the leadsDto as an argument
        leadsService.createLeads(leadsDto);
        return "redirect:/leads";
    }

    // Update detail page
    @PostMapping("/leads/detail/{leadId}/update")
    public String leadsUpdate(@PathVariable("leadId") Long leadId, @ModelAttribute LeadsDto leadsDto) {
        leadsService.updateLeads(leadId, leadsDto);
        return "redirect:/leads/detail/" + leadId;
    }

    // Delete detail page
    @GetMapping("/leads/detail/{leadId}/delete")
    public String leadsDeleteDetail(@PathVariable("leadId") Long leadId) {
        leadsService.deleteLeads(leadId);

        return "redirect:/leads";
    }

    // Delete read page (list)
    @PostMapping("/leads/detail/delete")
    public ResponseEntity<Void> deleteLeads(@RequestBody Map<String, List<Long>> request) {
        List<Long> ids = request.get("ids");
        System.out.println("deleteLeads Received IDs: " + ids); // 로그 추가
        leadsService.deleteLeadsByIds(ids);
        return ResponseEntity.ok().build(); // 상태 코드 200 반환
    }

}


