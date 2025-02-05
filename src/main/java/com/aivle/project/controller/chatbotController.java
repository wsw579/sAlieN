package com.aivle.project.controller;

import com.aivle.project.dto.*;
import com.aivle.project.entity.*;
import com.aivle.project.repository.AccountRepository;
import com.aivle.project.repository.EmployeeRepository;
import com.aivle.project.service.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class chatbotController {

    private final AccountService accountService;
    private final EmployeeService employeeService;
    private final EmployeeRepository employeeRepository;
    private final AccountRepository accountRepository;
    private final ProductsService productsService;
    private final LeadsService leadsService;
    private final OpportunitiesService opportunitiesService;
    private final ContractsService contractsService;

    @GetMapping("/chatbot/create/leads")
    public String createLeads(@RequestParam Map<String,String> params, Model model) {

        LeadsEntity leads = new LeadsEntity();
        List<AccountDto> accounts = accountService.getAllAccountIdsAndNames();
        List<EmployeeDto.GetId> employee = employeeService.getAllEmployeeIdsAndNamesAndDepartmentIds();

        String createdDate = params.get("createdDate");
        if (!createdDate.isEmpty()) {
            leads.setCreatedDate(LocalDate.parse(createdDate));
        } else{
            leads.setCreatedDate(LocalDate.now());
        }

        String targetCloseDate = params.get("targetCloseDate");
        if (!targetCloseDate.isEmpty()) {
            leads.setTargetCloseDate(LocalDate.parse(targetCloseDate));
        } else {
            leads.setTargetCloseDate(LocalDate.now());
        }

        leads.setLeadSource(params.get("leadSource"));
        leads.setCustomerRepresentitive(params.get("accountManager"));
        leads.setCustomerRequirements(params.get("customerRequirements"));
        leads.setC_tel(params.get("contact"));
        leads.setLeadStatus(params.get("leadStatus"));


        model.addAttribute("leads", leads);
        model.addAttribute("accounts", accounts);
        model.addAttribute("employee", employee);

        // 로깅
        params.forEach((key, value) -> System.out.println(key + ": " + value));

        return "leads/leads_detail";
    }

    @GetMapping("/chatbot/create/accounts")
    public String createAccount(@RequestParam Map<String,String> params, Model model) {

        AccountEntity account = new AccountEntity();
        List<EmployeeEntity> employee = employeeRepository.findAll();
        List<AccountEntity> activeAccounts = accountRepository.findByAccountStatus("Active");


        String createdDate = params.get("accountCreatedDate");
        if (!createdDate.isEmpty()) {
            account.setAccountCreatedDate(LocalDate.parse(createdDate));
        } else{
            account.setAccountCreatedDate(LocalDate.now());
        }

        account.setAccountName(params.get("accountName"));
        account.setWebsite(params.get("website"));
        account.setContact(params.get("contact"));
        account.setAccountManager(params.get("accountManager"));
        account.setAccountDetail(params.get("accountDetail"));
        account.setAddress(params.get("address"));
        account.setAccountManagerContact(params.get("accountManagerContact"));


        model.addAttribute("account", account);
        model.addAttribute("accounts", activeAccounts);
        model.addAttribute("employee", employee);

        // 로깅
        params.forEach((key, value) -> System.out.println(key + ": " + value));

        return "account/account_detail";
    }

    @GetMapping("/chatbot/create/opportunities")
    public String createOpportunity(@RequestParam Map<String,String> params, Model model) {

        OpportunitiesEntity opportunities = new OpportunitiesEntity();
        List<ProductsDto> products = productsService.getAllProductIdsAndNames();
        List<AccountDto> accounts = accountService.getAllAccountIdsAndNames();
        List<EmployeeDto.GetId> employee = employeeService.getAllEmployeeIdsAndNamesAndDepartmentIds();
        List<LeadsDto> leads = leadsService.getAllLeadIdsAndCompanyNames();


        String createdDate = params.get("createdDate");
        if (!createdDate.isEmpty()) {
            opportunities.setCreatedDate(LocalDate.parse(createdDate));
        } else{
            opportunities.setCreatedDate(LocalDate.now());
        }

        String targetCloseDate = params.get("targetCloseDate");
        if (!targetCloseDate.isEmpty()) {
            opportunities.setTargetCloseDate(LocalDate.parse(targetCloseDate));
        } else {
            opportunities.setTargetCloseDate(LocalDate.now());
        }

        String companyRevenue = params.get("companyRevenue");
        if (!companyRevenue.isEmpty()) {
            opportunities.setCompanyRevenue(Float.parseFloat(companyRevenue));
        } else {
            opportunities.setCompanyRevenue(0.0f);
        }

        String quantity = params.get("quantity");
        if (!quantity.isEmpty()) {
            opportunities.setQuantity(Float.parseFloat(quantity));
        } else {
            opportunities.setQuantity(0.0f);
        }

        String expectedRevenue = params.get("expectedRevenue");
        if (!expectedRevenue.isEmpty()) {
            opportunities.setExpectedRevenue(Float.parseFloat(expectedRevenue));
        } else {
            opportunities.setExpectedRevenue(0.0f);
        }



        opportunities.setOpportunityName(params.get("opportunityName"));
        opportunities.setSuccessRate(params.get("successRate"));
        opportunities.setRegion(params.get("region"));
        opportunities.setCustomerEmployee(params.get("customerEmployee"));
        opportunities.setOpportunityInquiries(params.get("opportunityInquiries"));
        opportunities.setOpportunityNotes(params.get("opportunityNotes"));


        model.addAttribute("opportunities", opportunities);
        model.addAttribute("leads", leads);
        model.addAttribute("accounts", accounts);
        model.addAttribute("employees", employee);
        model.addAttribute("products", products);

        // 로깅
        params.forEach((key, value) -> System.out.println(key + ": " + value));

        return "opportunities/opportunities_detail";
    }

    @GetMapping("/chatbot/create/contracts")
    public String createContract(@RequestParam Map<String,String> params, Model model) {

        ContractsEntity contract = new ContractsEntity();
        List<EmployeeEntity> employee = employeeRepository.findAll();
        List<AccountEntity> activeAccounts = accountRepository.findByAccountStatus("Active");
        List<ProductsDto> products = productsService.getAllProductIdsAndNames();
        List<OpportunitiesDto> opportunities = opportunitiesService.getAllOpportunityIdsAndNames();

        String startDate = params.get("startDate");
        if (!startDate.isEmpty()) {
            contract.setStartDate(LocalDate.parse(startDate));
        } else{
            contract.setStartDate(LocalDate.now());
        }

        String terminationDate = params.get("terminationDate");
        if (!terminationDate.isEmpty()) {
            contract.setTerminationDate(LocalDate.parse(terminationDate));
        } else {
            contract.setTerminationDate(LocalDate.now());
        }

        String contractAmount = params.get("contractAmount");
        if (!contractAmount.isEmpty()) {
            contract.setContractAmount(Float.parseFloat(contractAmount));
        } else {
            contract.setContractAmount(0.0f);
        }

        String contractSales = params.get("contractSales");
        if (!contractSales.isEmpty()) {
            contract.setContractSales(Float.parseFloat(contractSales));
        } else {
            contract.setContractSales(0.0f);
        }

        contract.setContractDetail(params.get("contractDetail"));

        model.addAttribute("contracts", contract);
        model.addAttribute("accounts", activeAccounts);//
        model.addAttribute("employee", employee);//
        model.addAttribute("products", products);
        model.addAttribute("opportunities", opportunities);


        // 로깅
        params.forEach((key, value) -> System.out.println(key + ": " + value));

        return "contracts/contracts_detail";
    }

    @GetMapping("/chatbot/create/orders")
    public String createOrders(@RequestParam Map<String,String> params, Model model) {

        OrdersEntity order = new OrdersEntity();
        List<ProductsDto> products = productsService.getAllProductIdsAndNames();
        List<ContractsDto> contracts = contractsService.getAllContractIds();


        String orderDate = params.get("orderDate");
        if (!orderDate.isEmpty()) {
            order.setOrderDate(LocalDate.parse(orderDate));
        } else{
            order.setOrderDate(LocalDate.now());
        }

        String salesDate = params.get("salesDate");
        if (!salesDate.isEmpty()) {
            order.setSalesDate(LocalDate.parse(salesDate));
        } else{
            order.setSalesDate(LocalDate.now());
        }

        String orderAmount = params.get("orderAmount");
        if (!orderAmount.isEmpty()) {
            order.setOrderAmount(Float.parseFloat(orderAmount));
        } else {
            order.setOrderAmount(0.0f);
        }

        model.addAttribute("orders", order);
        model.addAttribute("products", products);
        model.addAttribute("contracts", contracts);

        // 로깅
        params.forEach((key, value) -> System.out.println(key + ": " + value));

        return "orders/orders_detail";
    }






    @PostMapping("/chatbot/create/leads")
    public String createLeads(Model model, @RequestBody ChatbotDto.RequestData requestData) {

        // 요청 데이터 출력
        System.out.println("Response: " + requestData);

        // JSON 데이터를 문자열로 변환
        String jsonResponse = requestData.getResponse();

        // JSON 데이터를 Map으로 변환 (수동 파싱)
        Map<String, String> dataMap = parseJsonToMap(jsonResponse);

        // 현재 날짜 가져오기
        LocalDate today = LocalDate.now();
        System.out.println("get(c_tel)"+dataMap.get("c_tel"));
        System.out.println("getOrDefault(c_tel)"+dataMap.get("c_tel"));
//        // LeadsEntity 객체 생성 및 값 설정
        LeadsEntity lead = new LeadsEntity();
        lead.setLeadStatus(dataMap.getOrDefault("leadStatus", "Proposal")); // 기본값 "Proposal"
        lead.setLeadSource(dataMap.getOrDefault("leadSource", "chatbot")); // 기본값 "chatbot"
        lead.setCreatedDate(today); // 현재 날짜로 설정
        lead.setTargetCloseDate(today.plusDays(30)); // 기본값 현재 날짜 + 30일
        lead.setCustomerRequirements(dataMap.getOrDefault("customerRequirements", "N/A")); // 기본값 "N/A"
        lead.setCompanyName(dataMap.getOrDefault("companyName", "Unknown")); // 기본값 "Unknown"
        lead.setCustomerRepresentitive(dataMap.getOrDefault("customerRepresentitive", "미배정")); // 기본값 "미배정"
        lead.setC_tel(dataMap.getOrDefault("c_tel", "N/A")); // 기본값 "N/A"
//
//        // Opportunities 및 Employee, Account 설정은 상황에 따라 추가 로직 필요
//        lead.setOpportunities(null); // 기본값으로 null 설정
//        lead.setEmployeeId(null); // 기본값으로 null 설정
//        lead.setAccountId(null); // 기본값으로 null 설정

        model.addAttribute("leads", lead);
        // 클라이언트에 응답
//        return "/leads/leads_detail";
        return "redirect:/leads/detail/create";
    }

    @PostMapping("/chatbot/create/opportunities")
    public String createOpportunities(Model model, @RequestBody ChatbotDto.RequestData requestData) {

        // 요청 데이터 출력
        System.out.println("Response: " + requestData);

        // JSON 데이터를 문자열로 변환
        String jsonResponse = requestData.getResponse();

        // JSON 데이터를 Map으로 변환 (수동 파싱)
        Map<String, String> dataMap = parseJsonToMap(jsonResponse);

        // 현재 날짜 가져오기
        LocalDate today = LocalDate.now();

        // 클라이언트에 응답
        return "/opportunities/opportunities_detail";
    }

    @PostMapping("/chatbot/create/opportunitiesHistory")
    public String createOpportunitiesHistory(Model model, @RequestBody ChatbotDto.RequestData requestData) {

        // 요청 데이터 출력
        System.out.println("Response: " + requestData);

        // JSON 데이터를 문자열로 변환
        String jsonResponse = requestData.getResponse();

        // JSON 데이터를 Map으로 변환 (수동 파싱)
        Map<String, String> dataMap = parseJsonToMap(jsonResponse);

        // 현재 날짜 가져오기
        LocalDate today = LocalDate.now();

        // 클라이언트에 응답
        return "/opportunities/opportunities_history_detail";
    }

    @PostMapping("/chatbot/create/accounts")
    public String createAccounts(Model model, @RequestBody ChatbotDto.RequestData requestData) {

        // 요청 데이터 출력
        System.out.println("Response: " + requestData);

        // JSON 데이터를 문자열로 변환
        String jsonResponse = requestData.getResponse();

        // JSON 데이터를 Map으로 변환 (수동 파싱)
        Map<String, String> dataMap = parseJsonToMap(jsonResponse);

        // 현재 날짜 가져오기
        LocalDate today = LocalDate.now();

        // 클라이언트에 응답
        return "/account/account_detail";
    }

    @PostMapping("/chatbot/create/products")
    public String createProducts(Model model, @RequestBody ChatbotDto.RequestData requestData) {

        // 요청 데이터 출력
        System.out.println("Response: " + requestData);

        // JSON 데이터를 문자열로 변환
        String jsonResponse = requestData.getResponse();

        // JSON 데이터를 Map으로 변환 (수동 파싱)
        Map<String, String> dataMap = parseJsonToMap(jsonResponse);

        // 현재 날짜 가져오기
        LocalDate today = LocalDate.now();

        // 클라이언트에 응답
        return "/products/products_detail";
    }

    @PostMapping("/chatbot/create/orders")
    public String createOrders(Model model, @RequestBody ChatbotDto.RequestData requestData) {

        // 요청 데이터 출력
        System.out.println("Response: " + requestData);

        // JSON 데이터를 문자열로 변환
        String jsonResponse = requestData.getResponse();

        // JSON 데이터를 Map으로 변환 (수동 파싱)
        Map<String, String> dataMap = parseJsonToMap(jsonResponse);

        // 현재 날짜 가져오기
        LocalDate today = LocalDate.now();

        // 클라이언트에 응답
        return "/orders/orders_detail";
    }

    @PostMapping("/chatbot/create/contracts")
    public String createContracts(Model model, @RequestBody ChatbotDto.RequestData requestData) {

        // 요청 데이터 출력
        System.out.println("Response: " + requestData);

        // JSON 데이터를 문자열로 변환
        String jsonResponse = requestData.getResponse();

        // JSON 데이터를 Map으로 변환 (수동 파싱)
        Map<String, String> dataMap = parseJsonToMap(jsonResponse);

        // 현재 날짜 가져오기
        LocalDate today = LocalDate.now();

        // 클라이언트에 응답
        return "/contracts/contracts_detail";
    }


    private Map<String, String> parseJsonToMap(String json) {
        Map<String, String> map = new HashMap<>();

        // JSON 문자열에서 중괄호 제거
        json = json.trim();
        if (json.startsWith("{") && json.endsWith("}")) {
            json = json.substring(1, json.length() - 1);
        }

        // 키-값 쌍으로 나누기
        String[] keyValuePairs = json.split(",");
        for (String pair : keyValuePairs) {
            // 각 키-값 쌍의 공백 제거
            pair = pair.trim();

            // 키와 값 분리
            String[] keyValue = pair.split(":", 2); // ':'를 기준으로 최대 2개로 나눔
            if (keyValue.length == 2) {
                String key = keyValue[0].trim().replaceAll("^\"|\"$", ""); // 양쪽 따옴표 제거
                String value = keyValue[1].trim().replaceAll("^\"|\"$", ""); // 양쪽 따옴표 제거

                // "null" 문자열을 실제 null 값으로 처리
                if ("null".equalsIgnoreCase(value)) {
                    map.put(key, null);
                } else {
                    map.put(key, value);
                }
            }
        }
        return map;
    }
}
