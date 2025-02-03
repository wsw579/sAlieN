package com.aivle.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

@Controller
public class NotebookController {


    @GetMapping("/notebook")
    public String startNotebook(Model model) {


        String notebookUrl = "https://saiescrm.jupyterhub.jyds.synology.me/user/admin/doc/tree/Data_Analysis.ipynb";


        model.addAttribute("notebookUrl", notebookUrl);


        return "notebook/notebook";
    }


}
