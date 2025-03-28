package org.example.profilecase5.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class LogoutController {

    @GetMapping("/perform_logout")
    public String logout() {
        return "redirect:/login";  
    }
}
