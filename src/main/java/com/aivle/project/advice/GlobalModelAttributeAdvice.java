package com.aivle.project.advice;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Collection;
import java.util.Iterator;

@ControllerAdvice
public class GlobalModelAttributeAdvice {
//    @ModelAttribute
//    public void addUserIdToModel(Model model) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication != null && authentication.isAuthenticated() && !authentication.getName().equals("anonymousUser")) {
//            model.addAttribute("id", authentication.getName()); // 인증된 사용자 ID 전달
//        } else {
//            model.addAttribute("id", null); // 비로그인 상태
//        }
//    }
    @ModelAttribute
    public void addUserIdAndAuthoritiesToModel(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !authentication.getName().equals("anonymousUser")) {
            // 인증된 사용자 ID 전달
            model.addAttribute("id", authentication.getName());

            // 인증된 사용자의 권한 정보 전달
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            Iterator<? extends GrantedAuthority> iter = authorities.iterator();
            GrantedAuthority auth = iter.next();
            String role = auth.getAuthority();

            if(role.equals("ROLE_ADMIN")){
                model.addAttribute("isAdmin", true); // 권한 정보 전달
            }
        } else {
            model.addAttribute("id", null); // 비로그인 상태
        }
    }
}
