package com.aivle.project.controller;

import com.aivle.project.dto.ContractsDto;
import com.aivle.project.entity.ContractsEntity;
import com.aivle.project.service.ContractsService;
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
public class ContractsController {
    private final ContractsService contractsService;


    // Read page
    @GetMapping("/contracts")
    public String contracts(Model model) {
        List<ContractsEntity> contracts = contractsService.readContracts();

        // 데이터가 null이면 빈 리스트로 초기화
        if (contracts == null) {
            contracts = new ArrayList<>();
        }

        model.addAttribute("contracts", contracts);
        return "contracts/contracts_read";
    }

    // Detail page
    @GetMapping("/contracts/detail/{contractId}")
    public String contracts(@PathVariable Long contractId, Model model) {
        ContractsEntity contracts = contractsService.searchContracts(contractId);
        //List<OrderEntity> orders = contractsService.getOrdersByContractId(contractId);

        // 디버깅을 위해 로그 출력
        System.out.println("Contracts: " + contracts);
        //orders.forEach(order -> System.out.println("Comment: " + order.getOrderId() + ", Date: " + order.getOrderDate()));

        model.addAttribute("contracts", contracts);
        //model.addAttribute("orders", orders);
        return "contracts/contracts_detail";
    }

    // create order
//    @PostMapping("/contracts/detail/createorder")
//    public String createOrder(@RequestParam("content") String content, @RequestParam("contractId") Long opportunityId) {
//        opportunitiesService.createComment(content, opportunityId, "작성자"); // 작성자 이름을 실제로 설정
//        return "redirect:/opportunities/detail/" + opportunityId + "#commentSection";
//    }



    // Create model page (초기값으로 페이지 생성)
    @GetMapping("/contracts/detail/create")
    public String contractsCreate(Model model) {

        ContractsEntity contracts = new ContractsEntity();

        contracts.setContractStatus("Draft");
        contracts.setStartDate(LocalDate.now());
        contracts.setTerminationDate(LocalDate.now());
        contracts.setContractDetail("");
        contracts.setContractSales(0);
        contracts.setContractAmount(0);
        contracts.setContractClassification("");

        model.addAttribute("contracts", contracts);

        return "contracts/contracts_detail";
    }

    @PostMapping("/contracts/detail/create")
    public String contractsCreateNew(@ModelAttribute ContractsDto contractsDto) {

        contractsService.createContracts(contractsDto);

        return "redirect:/contracts";
    }




    // Update detail page
    @PostMapping("/contracts/detail/{contractId}/update")
    public String contractsUpdate(@PathVariable("contractId") Long contractId, @ModelAttribute ContractsDto contractsDto) {
        contractsService.updateContracts(contractId, contractsDto);
        return "redirect:/contracts/detail/" + contractId;
    }

    // Delete detail page
    @GetMapping("/contracts/detail/{contractId}/delete")
    public String opportunitiesDeleteDetail(@PathVariable("contractId") Long contractId) {
        contractsService.deleteContracts(contractId);

        return "redirect:/contracts";
    }

    // Delete read page (list)
    @PostMapping("/contracts/detail/delete")
    public ResponseEntity<Void> deleteContracts(@RequestBody Map<String, List<Long>> request) {
        List<Long> ids = request.get("ids");
        System.out.println("deleteContracts Received IDs: " + ids); // 로그 추가
        contractsService.deleteContractsByIds(ids);
        return ResponseEntity.ok().build(); // 상태 코드 200 반환
    }




}
