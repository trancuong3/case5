package org.example.profilecase5.Controller;

import org.apache.tomcat.util.codec.binary.Base64;
import org.example.profilecase5.Model.User;
import org.example.profilecase5.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.nio.file.Files;

@Controller
@RequestMapping("/hosting")
public class HostingController {

    @Autowired
    private UserService userService;

    @GetMapping("")
    public String getAccountPage(Model model, Authentication authentication) {
        String username = authentication.getName(); 
        User user = userService.getUserByUsername(username);

        if (user == null) {
            model.addAttribute("error", "User not found");
            return "error"; 
        }

        if (user.getAvatar() == null || user.getAvatar().isEmpty()) {
            user.setAvatar(null);
        }

        model.addAttribute("user", user);
        return "hosting/hosting";  
    }
}
