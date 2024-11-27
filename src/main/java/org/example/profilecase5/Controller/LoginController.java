package org.example.profilecase5.Controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.example.profilecase5.Model.User;
import org.example.profilecase5.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private UserService userService;

    @GetMapping
    public String login(@ModelAttribute("error") String error, Model model) {
        model.addAttribute("error", error);
        return "login/login";
    }



    @PostMapping("/perform_login")
    public String processLogin(@RequestParam String username,
                               @RequestParam String password,
                               @RequestParam String selectedRole,
                               RedirectAttributes redirectAttributes,
                               HttpServletRequest request) {
        try {
            // Kiểm tra thông tin đăng nhập và role
            boolean isValid = userService.validateUserAndRole(username, password, selectedRole);
            boolean isActive = userService.isActive(username);
            if (isValid ) {
                if(isActive) {
                    // Lấy đối tượng Authentication để xác thực người dùng
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(username, password);

                    // Cập nhật Authentication trong SecurityContext
                    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    // Lấy thông tin người dùng từ Authentication
                    User user = userService.getUserByUsername(username);  // Tìm người dùng từ cơ sở dữ liệu
                    HttpSession session = request.getSession();

                    // Lưu id người dùng vào session
                    session.setAttribute("userId", user.getUserId());

                    // Điều hướng dựa trên vai trò người dùng
                    switch (selectedRole) {
                        case "ROLE_USER":
                            return "redirect:/profile";
                        case "ROLE_ADMIN":
                            return "redirect:/admin";
                        case "ROLE_HOST":
                            return "redirect:/hosting";
                        // Nếu vai trò không hợp lệ, quay lại trang đăng nhập
                        default:
                            return "redirect:/login?error=true";
                    }
                }
                else {
                    redirectAttributes.addFlashAttribute("error", "Tài khoản của bạn đã bị khóa");
                    return "redirect:/login?error=true";
                }

            } else {
                redirectAttributes.addFlashAttribute("error", "Role không hợp lệ");
                return "redirect:/login?error=true";
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An error occurred during login");
            return "redirect:/login?error=true";
        }
    }
}
