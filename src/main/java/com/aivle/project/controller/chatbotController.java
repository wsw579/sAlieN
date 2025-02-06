package com.aivle.project.controller;

import com.aivle.project.dto.*;
import com.aivle.project.entity.*;
import com.aivle.project.enums.OrderStatus;
import com.aivle.project.repository.*;
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
    private final ProductsRepository productsRepository;
    private final OpportunitiesRepository opportunitiesRepository;
    private final ContractsRepository contractsRepository;

    @GetMapping("/chatbot/create/leads")
    public String createLeads(@RequestParam Map<String,String> params, Model model) {

        LeadsEntity leads = new LeadsEntity();
        AccountEntity account = new AccountEntity();

        EmployeeEntity employee = employeeRepository.findById(params.get("employeeId")).get();

        String accountId = params.get("accountId");
        if(!accountId.isEmpty()) {
            account = accountRepository.findById((long) Double.parseDouble(accountId)).get();
        }

        leads.setAccountId(account);
        leads.setEmployeeId(employee);

        List<AccountDto> accounts = accountService.getAllAccountIdsAndNames();
        List<EmployeeDto.GetId> employees = employeeService.getAllEmployeeIdsAndNamesAndDepartmentIds();

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
//        leads.setCustomerRepresentitive(params.get("accountManager"));
        leads.setCustomerRequirements(params.get("customerRequirements"));
        leads.setC_tel(params.get("contact"));
        leads.setLeadStatus(params.get("leadStatus"));
        leads.setCompanyName(params.get("companyName"));
        leads.setCustomerRepresentitive(params.get("customerRepresentitive"));


        model.addAttribute("leads", leads);
        model.addAttribute("accounts", accounts);
        model.addAttribute("employee", employees);

        // 로깅
        params.forEach((key, value) -> System.out.println(key + ": " + value));

        return "leads/chatbot_leads_detail";
    }

    @GetMapping("/chatbot/create/accounts")
    public String createAccount(@RequestParam Map<String,String> params, Model model) {

        AccountEntity account = new AccountEntity();
        EmployeeEntity employee = employeeRepository.findById(params.get("employeeId")).get();
        AccountEntity parentAccount = new AccountEntity();

        String parentAccountId = params.get("parentAccount");
        if(!parentAccountId.isEmpty()) {
            parentAccount = accountRepository.findById((long) Double.parseDouble(parentAccountId)).get();
        }

        account.setEmployeeId(employee);
        account.setParentAccount(parentAccount);

        List<EmployeeEntity> employees = employeeRepository.findAll();
        List<AccountEntity> activeAccounts = accountRepository.findByAccountStatus("Active");
        List<AccountEntity> parent = accountRepository.findByAccountStatus("Active");


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
        account.setAccountStatus(params.get("accountStatus"));
        account.setAccountType(params.get("accountType"));
        account.setBusinessType(params.get("businessType"));


        model.addAttribute("account", account);
        model.addAttribute("accounts", activeAccounts);
        model.addAttribute("employee", employees);
        model.addAttribute("parent", parent);

        // 로깅
        params.forEach((key, value) -> System.out.println(key + ": " + value));

        return "account/chatbot_account_detail";
    }

    @GetMapping("/chatbot/create/opportunities")
    public String createOpportunity(@RequestParam Map<String,String> params, Model model) {

        OpportunitiesEntity opportunities = new OpportunitiesEntity();
        List<ProductsDto> products = productsService.getAllProductIdsAndNames();
        List<AccountDto> accounts = accountService.getAllAccountIdsAndNames();
        List<EmployeeDto.GetId> employees = employeeService.getAllEmployeeIdsAndNamesAndDepartmentIds();
        List<LeadsDto> leads = leadsService.getAllLeadIdsAndCompanyNames();

        EmployeeEntity employee = employeeRepository.findById(params.get("employeeId")).get();
        AccountEntity account = accountRepository.findById((long) Double.parseDouble(params.get("accountId"))).get();
        ProductsEntity product = productsRepository.findById(Long.parseLong(params.get("productId"))).get();

        opportunities.setEmployeeId(employee);
        opportunities.setAccountId(account);
        opportunities.setProductId(product);


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
        opportunities.setOpportunityStatus(params.get("opportunityStatus"));

        model.addAttribute("opportunities", opportunities);
        model.addAttribute("leads", leads);
        model.addAttribute("accounts", accounts);
        model.addAttribute("employee", employees);
        model.addAttribute("products", products);

        // 로깅
        params.forEach((key, value) -> System.out.println(key + ": " + value));

        return "opportunities/chatbot_opportunities_detail";
    }

    @GetMapping("/chatbot/create/contracts")
    public String createContract(@RequestParam Map<String,String> params, Model model) {

        ContractsEntity contract = new ContractsEntity();
        List<EmployeeEntity> employees = employeeRepository.findAll();
        List<AccountEntity> activeAccounts = accountRepository.findByAccountStatus("Active");
        List<ProductsDto> products = productsService.getAllProductIdsAndNames();
        List<OpportunitiesDto> opportunities = opportunitiesService.getAllOpportunityIdsAndNames();

        EmployeeEntity employee = employeeRepository.findById(params.get("employeeId")).get();
        AccountEntity account = accountRepository.findById((long) Double.parseDouble(params.get("accountId"))).get();
        ProductsEntity product = productsRepository.findById(Long.parseLong(params.get("productId"))).get();
        OpportunitiesEntity opportunity = opportunitiesRepository.findById(Long.parseLong(params.get("opportunityId"))).get();

        contract.setEmployeeId(employee);
        contract.setAccountId(account);
        contract.setProductId(product);
        contract.setOpportunityId(opportunity);


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
        contract.setContractStatus(params.get("contractStatus"));
        contract.setContractClassification(params.get("contractClassification"));

        model.addAttribute("contracts", contract);
        model.addAttribute("accounts", activeAccounts);//
        model.addAttribute("employee", employees);//
        model.addAttribute("products", products);
        model.addAttribute("opportunities", opportunities);


        // 로깅
        params.forEach((key, value) -> System.out.println(key + ": " + value));

        return "contracts/chatbot_contracts_detail";
    }

    @GetMapping("/chatbot/create/orders")
    public String createOrders(@RequestParam Map<String,String> params, Model model) {

        OrdersEntity order = new OrdersEntity();
        List<ProductsDto> products = productsService.getAllProductIdsAndNames();
        List<ContractsDto> contracts = contractsService.getAllContractIds();

        ContractsEntity contract = contractsRepository.findById(Long.parseLong(params.get("contractId"))).get();
        ProductsEntity product = productsRepository.findById(Long.parseLong(params.get("productId"))).get();


        order.setContractId(contract);
        order.setProductId(product);

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

        String orderStatus = params.get("orderStatus");
        if (!orderStatus.isEmpty()) {
            order.setOrderStatus(OrderStatus.valueOf(params.get("orderStatus")));
        } else {
            order.setOrderStatus(OrderStatus.valueOf("activated"));
        }



        model.addAttribute("orders", order);
        model.addAttribute("products", products);
        model.addAttribute("contracts", contracts);

        // 로깅
        params.forEach((key, value) -> System.out.println(key + ": " + value));

        return "orders/chatbot_orders_detail";
    }

}
