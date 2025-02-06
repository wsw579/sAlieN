package com.aivle.project.controller;

import com.aivle.project.dto.AccountDto;
import com.aivle.project.dto.EmployeeDto;
import com.aivle.project.dto.LeadsDto;
import com.aivle.project.dto.PaginationDto;
import com.aivle.project.entity.*;
import com.aivle.project.service.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;


@Controller
//automatically generates a constructor for all final fields in the class
@RequiredArgsConstructor
public class LeadsController {
    // declares a dependency on the LeadsService class
    private final LeadsService leadsService;
    private final AccountService accountService;
    private final EmployeeService employeeService;
    private final PaginationService paginationService;
    private final CrudLogsService crudLogsService;

    // Read Page
    @GetMapping("/leads")
    public String leads(@RequestParam Map<String, String> params, Model model) {
        int page = Integer.parseInt(params.getOrDefault("page", "0"));
        int size = Integer.parseInt(params.getOrDefault("size", "10"));
        String search = params.getOrDefault("search", "");
        String sortColumn = params.getOrDefault("sortColumn", "createdDate");
        String sortDirection = params.getOrDefault("sortDirection", "desc");


        Page<LeadsEntity> leadsPage = leadsService.readLeads(page, size, search, sortColumn, sortDirection);

        // 페이지네이션 데이터 생성
        PaginationDto<LeadsEntity> paginationDto = paginationService.createPaginationData(leadsPage, page, 5);


        // 상태별 주문 개수 가져오기
        Map<String, Long> statusCounts = leadsService.getLeadStatusCounts();
        long allCount = statusCounts.values().stream().mapToLong(Long::longValue).sum();

        // Model에 데이터 추가
        model.addAttribute("pagination", paginationDto);

        // 검색 및 정렬 데이터
        model.addAttribute("search", search); // 검색어
        model.addAttribute("sortColumn", sortColumn); // 정렬 기준
        model.addAttribute("sortDirection", sortDirection); // 정렬 방향
        // Mustache 렌더링에 필요한 플래그 추가
        model.addAttribute("isCreatedDateSorted", "createdDate".equals(sortColumn)); // 정렬 기준이 orderDate인지
        model.addAttribute("isTargetCloseDateSorted", "targetCloseDate".equals(sortColumn)); // 정렬 기준이 orderAmount인지
        model.addAttribute("isAscSorted", "asc".equals(sortDirection)); // 정렬 방향이 asc인지
        model.addAttribute("isDescSorted", "desc".equals(sortDirection)); // 정렬 방향이 desc인지
        model.addAttribute("allCount", allCount);

        // 상태별 개수 추가
        model.addAttribute("proposalCount", statusCounts.getOrDefault("Proposal", 0L));
        model.addAttribute("reviewCount", statusCounts.getOrDefault("Under Review", 0L));
        model.addAttribute("acceptedCount", statusCounts.getOrDefault("Accepted", 0L));
        return "leads/leads_read";
    }

    @GetMapping("/leads/bar-data")
    public ResponseEntity<Map<String, List<Integer>>> getBarData() {
        return ResponseEntity.ok(leadsService.getBarData());
    }

    @GetMapping("/leads/chart-data")
    public ResponseEntity<Map<String, List<Integer>>> getChartData() {
        return ResponseEntity.ok(leadsService.getChartData());
    }

    //Detail page
    @GetMapping("/leads/detail/{leadId}")
    public String leads(@PathVariable Long leadId, Model model){
        LeadsEntity leads = leadsService.searchLeads(leadId);

        List<AccountDto> accounts = accountService.getAllAccountIdsAndNames();
        List<EmployeeDto.GetId> employee = employeeService.getAllEmployeeIdsAndNamesAndDepartmentIds();



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

        // 로딩속도를 올리기 위해 findAll -> id와 name만 가져오게 변경
        List<AccountDto> accounts = accountService.getAllAccountIdsAndNames();
        List<EmployeeDto.GetId> employee = employeeService.getAllEmployeeIdsAndNamesAndDepartmentIds();

        leads.setLeadStatus("");
        leads.setLeadSource("");
        leads.setCreatedDate(LocalDate.now());
        leads.setTargetCloseDate(LocalDate.now());
        leads.setCustomerRequirements("");
        leads.setCustomerRepresentitive("");
        leads.setCompanyName("");
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
    public String leadsCreateNew(@ModelAttribute LeadsDto leadsDto, RedirectAttributes redirectAttributes){
        try {
            // 리드 생성
            leadsService.createLeads(leadsDto);

            // CRUD 작업 로깅
            crudLogsService.logCrudOperation("create", "leads", "", "True", "Success");

            // 성공 메시지를 RedirectAttributes에 저장 (리다이렉트 후에도 유지됨)
            redirectAttributes.addFlashAttribute("message", "리드가 성공적으로 생성되었습니다.");

            return "redirect:/leads"; // 성공 시 리드 목록 페이지로 이동
        } catch (Exception e) {
            // 실패 로그 기록
            crudLogsService.logCrudOperation("create", "leads", "", "False", "Error: " + e.getMessage());

            // 에러 메시지를 사용자에게 전달
            redirectAttributes.addFlashAttribute("errorMessage", "리드 생성 중 오류가 발생했습니다. 다시 시도해주세요.");

            return "redirect:/errorPage"; // 에러 발생 시 오류 페이지로 리다이렉트
        }
    }

    // Update detail page
    @PostMapping("/leads/detail/{leadId}/update")
    public String leadsUpdate(@PathVariable("leadId") Long leadId, @ModelAttribute LeadsDto leadsDto, RedirectAttributes redirectAttributes) {
        try {
            // 리드 수정
            leadsService.updateLeads(leadId, leadsDto);

            // 성공 로그 기록
            crudLogsService.logCrudOperation("update", "leads", leadId.toString(), "True", "Success");

            // 성공 메시지를 RedirectAttributes에 저장 (리다이렉트 후에도 유지됨)
            redirectAttributes.addFlashAttribute("message", "리드가 성공적으로 수정되었습니다.");

            return "redirect:/leads/detail/" + leadId;
        } catch (Exception e) {
            // 실패 로그 기록
            crudLogsService.logCrudOperation("update", "leads", leadId.toString(), "False", "Error: " + e.getMessage());

            // 에러 메시지를 사용자에게 전달
            redirectAttributes.addFlashAttribute("errorMessage", "리드 수정 중 오류가 발생했습니다. 다시 시도해주세요.");

            return "redirect:/errorPage"; // 에러 발생 시 오류 페이지로 리다이렉트
        }
    }

    // Delete detail page
    @PostMapping("/leads/detail/{leadId}/delete")
    public ResponseEntity<Void> deleteLead(@PathVariable("leadId") Long leadId) {
        try {
            // 리드 삭제 실행
            leadsService.deleteLeads(leadId);

            // CRUD 작업 로깅
            crudLogsService.logCrudOperation("delete", "leads", leadId.toString(), "True", "Success");

            return ResponseEntity.ok().build(); // HTTP 200 응답 (삭제 성공)
        } catch (Exception e) {
            // 삭제 실패 로그 기록
            crudLogsService.logCrudOperation("delete", "leads", leadId.toString(), "False", "Error: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // HTTP 500 응답 (삭제 실패)
        }
    }

    // Delete read page (list)
    @PostMapping("/leads/detail/delete")
    public ResponseEntity<Void> deleteLeads(@RequestBody Map<String, List<Long>> request) {
        List<Long> ids = request.get("ids");
        System.out.println("deleteLeads Received IDs: " + ids); // 로그 추가
        try {
            // 리드 삭제 실행
            leadsService.deleteLeadsByIds(ids);

            // 개별 ID에 대해 성공 로그 기록
            for (Long id : ids) {
                crudLogsService.logCrudOperation("delete", "leads", id.toString(), "True", "Success");
            }

            return ResponseEntity.ok().build(); // HTTP 200 응답 (삭제 성공)
        } catch (Exception e) {
            // 개별 ID에 대해 실패 로그 기록
            for (Long id : ids) {
                crudLogsService.logCrudOperation("delete", "leads", id.toString(), "False", "Error: " + e.getMessage());
            }

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // HTTP 500 응답 (삭제 실패)
        }
    }

//    // 오늘 추가된 Leads 수 반환
//    @GetMapping("/api/leads/today")
//    public ResponseEntity<Map<String, Object>> getTodayLeads() {
//        long count = leadsService.getTodayLeadsForTeam();
//        Map<String, Object> response = new HashMap<>();
//        response.put("todayLeads", count);
//        return ResponseEntity.ok(response);
//    }
//
//    // 특정 상태의 Leads 수 반환
//    @GetMapping("/api/leads/status")
//    public ResponseEntity<Map<String, Object>> countLeadsByStatus(@RequestParam String leadStatus) {
//        long count = leadsService.countLeadsByStatusAndTeam(leadStatus);
//        Map<String, Object> response = new HashMap<>();
//        response.put("leadStatus", leadStatus);
//        response.put("leadCount", count);
//        return ResponseEntity.ok(response);
//    }
//
//    // 오늘 마감인 Leads 수 반환
//    @GetMapping("/api/leads/target-close-today")
//    public ResponseEntity<Map<String, Object>> countLeadsWithTargetCloseDateToday() {
//        long count = leadsService.countLeadsWithTargetCloseDateTodayForTeam();
//        Map<String, Object> response = new HashMap<>();
//        response.put("targetCloseDate", "Today");
//        response.put("leadCount", count);
//        return ResponseEntity.ok(response);
//    }

    // AI 음성인식
    @PostMapping("/leads/auto")
    public ResponseEntity<Map<String, Object>> handleFileUpload(@RequestPart("audio_file") MultipartFile audio_file) {
        try {
            // MIME 타입 검증 (MP3와 WAV 파일 허용)
            String contentType = audio_file.getContentType();
            if (!"audio/mpeg".equals(contentType) && !"audio/wav".equals(contentType) && !"audio/x-wav".equals(contentType)) {
                throw new IllegalArgumentException("MP3 또는 WAV 파일만 업로드 가능합니다.");
            }


            // fastApiUrl
            String fastApiUrl = "http://127.0.0.1:8000/leads/auto";

            // RestTemplate 생성
            RestTemplate restTemplate = new RestTemplate();

            // HTTP 요청 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            // 파일 데이터를 바이트 배열로 변환
            byte[] fileBytes = audio_file.getBytes();

            // ByteArrayResource 생성
            ByteArrayResource resource = new ByteArrayResource(fileBytes) {
                @Override
                public String getFilename() {
                    return audio_file.getOriginalFilename(); // 원본 파일 이름 반환
                }
            };

            // 요청 본문 생성
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("audio_file", resource);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            // FastAPI 서버로 POST 요청 보내기
            ResponseEntity<String> response = restTemplate.postForEntity(fastApiUrl, requestEntity, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                // JSON 응답 파싱 및 반환
                Map<String, Object> responseBody = new HashMap<>();
                responseBody.put("data", response.getBody());
                return ResponseEntity.ok(responseBody);
            } else {
                return ResponseEntity.status(response.getStatusCode()).body(null);
            }
        } catch (IllegalArgumentException e) {
            // MIME 타입 검증 실패 시 처리
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }



}


