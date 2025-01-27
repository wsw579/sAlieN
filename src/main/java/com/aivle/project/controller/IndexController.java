package com.aivle.project.controller;

import com.aivle.project.entity.CrudLogsEntity;
import com.aivle.project.enums.Position;
import com.aivle.project.enums.Role;
import com.aivle.project.service.EmployeeService;
import com.aivle.project.utils.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;


@Controller
@RequiredArgsConstructor
@Slf4j
public class IndexController {
    private final EmployeeService employeeService;

    @GetMapping("/")
    public String index(Model model) {
        try {
            String userid = UserContext.getCurrentUserId();
            Role userRole = Role.valueOf(UserContext.getCurrentUserRole());
            Position userPosition = Position.valueOf(employeeService.getPositionByUserId(userid));

            // Role이 ROLE_ADMIN이거나, 특정 Position인 경우 admin 페이지 반환
            if (Role.ROLE_ADMIN.equals(userRole) ||
                    Position.GENERAL_MANAGER.equals(userPosition) ||
                    Position.DEPARTMENT_HEAD.equals(userPosition) ||
                    Position.TEAM_LEADER.equals(userPosition)) {
                return "main/index_manager";
            } else {
                return "main/index_user";
            }
        } catch (IllegalArgumentException | NullPointerException e) {
            // 역할이 없는 경우, 또는 잘못된 값일 경우 처리
            return "error/unauthorized"; // 에러페이지
        }
    }


    @GetMapping("/rate_plan")
    public String ratePlan(@ModelAttribute("id") String employeeId, Model model) {
        // 세션에서 employeeId 가져오기
        if (employeeId == null) {
            throw new IllegalArgumentException("Invalid employeeId: " + employeeId);
        }

        return "main/rate_plan";
    }



}
