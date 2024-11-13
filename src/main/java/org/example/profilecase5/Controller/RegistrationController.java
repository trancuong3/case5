package org.example.profilecase5.Controller;



import org.example.profilecase5.Exception.User.EmailAlreadyExistsException;
import org.example.profilecase5.Exception.User.UsernameAlreadyExistsException;
import org.example.profilecase5.Model.Owner;
import org.example.profilecase5.Model.User;
import org.example.profilecase5.Service.OwnerService;
import org.example.profilecase5.Service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.Instant;

@Controller
@RequestMapping("/register")
public class RegistrationController {
    private final UserService userService;
    private final OwnerService ownerService;


    public RegistrationController(UserService userService, OwnerService ownerService) {
        this.userService = userService;
        this.ownerService = ownerService;
    }

    @GetMapping
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register/register";
    }

    @PostMapping
    public String registerUser(@Validated @ModelAttribute("user") User user, BindingResult result, Model model) {
        if (result.hasErrors()) {
            System.out.println(result.getAllErrors());
            return "register/register";

        }

        // Thiết lập thời gian hiện tại cho createdAt và updatedAt
        Timestamp currentTimestamp = Timestamp.from(Instant.now());
        user.setCreatedAt(currentTimestamp);
        user.setUpdatedAt(currentTimestamp);

        try {
            userService.registerUser(user);
        } catch (UsernameAlreadyExistsException e) {
            result.rejectValue("username", "error.username", e.getMessage());
            return "register/register";

        } catch (EmailAlreadyExistsException e) {
            result.rejectValue("email", "error.email", e.getMessage());
            return "register/register";

        }
        return "redirect:/login";
    }
    @GetMapping("/owner")
    public String showOwnerRegistrationForm() {
        return "owner/register";
    }
    @PostMapping("/owner")
    public String registerOwnerUser(@RequestParam String password, Model model) {
        // Kiểm tra password
        if (ownerService.checkPassword(password)) {
            Timestamp currentTimestamp = Timestamp.from(Instant.now());
            Owner owner = new Owner();
            owner.setCreatedAt(currentTimestamp);
            owner.setUpdatedAt(currentTimestamp);
            ownerService.registerOwner(owner);
            return "redirect:/login/owner";
        } else {
            model.addAttribute("error", "Mật khẩu sai");
            return "owner/register";
        }
    }

}