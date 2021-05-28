package com.teamteach.journalmgmt.infra.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {
    @RequestMapping("/report")
    public String report() {
        return "report";
    }
}