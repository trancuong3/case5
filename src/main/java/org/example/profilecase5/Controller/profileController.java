package org.example.profilecase5.Controller;

import net.coobird.thumbnailator.Thumbnails;
import org.example.profilecase5.Model.User;
import org.example.profilecase5.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

@Controller
@RequestMapping("profile")
public class profileController {

    @Autowired
    private UserService userService;

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    @GetMapping("")
    public String getPage(Model model) {
        int userId = 1;
        User user = userService.getUserById(userId);
        if (user != null) {
            model.addAttribute("user", user);
            return "profile/profile";
        } else {
            model.addAttribute("error", "User not found");
            return "error";
        }
    }


    @PostMapping("/update-avatar")
    public String updateAvatar(@RequestParam("avatar") MultipartFile file, Model model) {
        try {
            if (file.getSize() > MAX_FILE_SIZE) {
                model.addAttribute("message", "Ảnh quá lớn, vui lòng chọn ảnh nhỏ hơn 5MB.");
                return "profile/profile";
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Thumbnails.of(file.getInputStream())
                    .size(200, 200)
                    .outputFormat("JPEG")
                    .outputQuality(0.8f)
                    .toOutputStream(outputStream);

            byte[] resizedImage = outputStream.toByteArray();
            String base64Avatar = Base64.getEncoder().encodeToString(resizedImage);

            User user = userService.getUserById(1);
            user.setAvatar(base64Avatar);
            userService.saveUser(user);
        } catch (IOException e) {

            model.addAttribute("message", "Đã xảy ra lỗi khi cập nhật ảnh đại diện. Vui lòng thử lại.");
        }
        return "redirect:/profile";
    }
}
