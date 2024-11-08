package org.example.profilecase5.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HostingController {

    @GetMapping("/hosting")
    public String showHostingPage(){
        return "hosting";
    }
}