package org.joonzis.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/support")
public class SupportController {

    @GetMapping("")
    public String support() {
        return "support/support";
    }

    @GetMapping("/{id}")
    public String supportDetail(@PathVariable Long id) {
        return "support/supportDetail";
    }
}