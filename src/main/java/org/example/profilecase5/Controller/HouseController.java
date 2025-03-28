package org.example.profilecase5.Controller;

import net.coobird.thumbnailator.Thumbnails;
import org.example.profilecase5.Model.House;
import org.example.profilecase5.Model.HouseImage;
import org.example.profilecase5.Model.User;
import org.example.profilecase5.Repository.HouseRepository;
import org.example.profilecase5.Service.HouseService;
import org.example.profilecase5.Service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import java.net.URL;
import java.io.InputStream;
import java.io.ByteArrayInputStream;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/house")
public class HouseController {
    @Autowired
    HouseRepository houseRepository;
    @Autowired
    private HouseService houseService;

    @Autowired
    private UserService userService;

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final Logger logger = LoggerFactory.getLogger(HouseController.class);
    private final String UPLOAD_DIR = "uploads/";

    @GetMapping("/new")
    public String showHouseForm(Model model, Authentication authentication) {
        String username = authentication.getName();
        User user = userService.getUserByUsername(username);

        if (user.getAvatar() == null) {
            user.setAvatar("/images/img_2.png");
        }

        model.addAttribute("user", user);
        model.addAttribute("house", new House());
        return "house/house_form";
    }


    @PostMapping("/new")
    public String saveHouse(@Valid @ModelAttribute("house") House house,
                            BindingResult bindingResult,
                            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                            @RequestParam(value = "imageUrl", required = false) String imageUrl,
                            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult.getAllErrors());
            logger.warn("Validation errors while submitting the form: {}", bindingResult.getAllErrors());
            return "house/house_form";
        }

        if ((imageFile == null || imageFile.isEmpty()) && (imageUrl == null || imageUrl.trim().isEmpty())) {
            model.addAttribute("errorMessage", "Vui lòng tải lên ảnh hoặc nhập URL.");
            return "house/house_form";
        }

        if ((imageFile != null && !imageFile.isEmpty()) && (imageUrl != null && !imageUrl.trim().isEmpty())) {
            model.addAttribute("errorMessage", "Chỉ được chọn một trong hai: tải lên ảnh hoặc nhập URL.");
            return "house/house_form";
        }

        try {
            if (imageFile != null && !imageFile.isEmpty()) {
                if (imageFile.getSize() > MAX_FILE_SIZE) {
                    model.addAttribute("errorMessage", "Ảnh quá lớn, vui lòng chọn ảnh nhỏ hơn 5MB.");
                    logger.warn("File size exceeds the limit: {} bytes", imageFile.getSize());
                    return "house/house_form";
                }

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                Thumbnails.of(imageFile.getInputStream())
                        .size(800, 800)
                        .outputFormat("JPEG")
                        .outputQuality(0.8f)
                        .toOutputStream(outputStream);

                byte[] resizedImage = outputStream.toByteArray();
                String base64Image = Base64.getEncoder().encodeToString(resizedImage);

                HouseImage houseImage = new HouseImage();
                houseImage.setImageUrl(base64Image);
                houseImage.setHouse(house);
                houseImage.setMain(true);

                house.getHouseImages().add(houseImage);
            }

            if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                if (imageUrl.startsWith("data:")) {
                    try {
                        String base64Image = imageUrl.split(",")[1]; 
                        byte[] imageBytes = Base64.getDecoder().decode(base64Image);

                      
                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        Thumbnails.of(new ByteArrayInputStream(imageBytes))
                                .size(800, 800)
                                .outputFormat("JPEG")
                                .outputQuality(0.8f)
                                .toOutputStream(outputStream);

                        byte[] resizedImage = outputStream.toByteArray();
                        String resizedBase64Image = Base64.getEncoder().encodeToString(resizedImage);

                        HouseImage houseImage = new HouseImage();
                        houseImage.setImageUrl(resizedBase64Image);
                        houseImage.setHouse(house);
                        houseImage.setMain(true);

                        house.getHouseImages().add(houseImage);
                    } catch (Exception e) {
                        logger.error("Error while processing data URL", e);
                        model.addAttribute("errorMessage", "Đã xảy ra lỗi khi xử lý ảnh từ URL.");
                        return "house/house_form";
                    }
                } else {
                    try {
                        URL url = new URL(imageUrl);
                        InputStream inputStream = url.openStream();

                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        Thumbnails.of(inputStream)
                                .size(800, 800)
                                .outputFormat("JPEG")
                                .outputQuality(0.8f)
                                .toOutputStream(outputStream);

                        byte[] resizedImage = outputStream.toByteArray();
                        String base64Image = Base64.getEncoder().encodeToString(resizedImage);

                        HouseImage houseImage = new HouseImage();
                        houseImage.setImageUrl(base64Image);
                        houseImage.setHouse(house);
                        houseImage.setMain(true);

                        house.getHouseImages().add(houseImage);
                    } catch (IOException e) {
                        logger.error("Error while processing image URL", e);
                        model.addAttribute("errorMessage", "Đã xảy ra lỗi khi xử lý ảnh từ URL.");
                        return "house/house_form";
                    }
                }
            }

        } catch (IOException e) {
            logger.error("Error while processing image file or URL", e);
            model.addAttribute("errorMessage", "Đã xảy ra lỗi khi xử lý ảnh. Vui lòng thử lại.");
            return "house/house_form";
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUserByUsername(authentication.getName());
        if (user.getAvatar() == null || user.getAvatar().isEmpty()) {
            user.setAvatar("/images/img_2.png");
        }

        house.setUser(user);

        try {
            houseService.saveHouse(house);
            logger.info("House saved successfully: {}", house);
        } catch (Exception e) {
            logger.error("Error while saving house to the database", e);
            model.addAttribute("errorMessage", "Có lỗi xảy ra khi lưu dữ liệu nhà.");
            return "house/house_form";
        }

        return "redirect:/hosting/listings";
    }




    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") int id, Model model, Authentication authentication) {
        if (authentication == null) {
            model.addAttribute("errorMessage", "Bạn cần đăng nhập để chỉnh sửa");
            return "redirect:/login"; 
        }

        String username = authentication.getName(); 
        User user = userService.getUserByUsername(username);

        if (user == null) {
            model.addAttribute("errorMessage", "Người dùng không tồn tại");
            return "redirect:/login"; 
        }

        if (user.getAvatar() == null) {
            user.setAvatar("/images/img_2.png");
        }

        model.addAttribute("user", user); 

        Optional<House> house = houseService.findById(id);
        if (house.isPresent()) {
            model.addAttribute("house", house.get());
            return "house/edit";
        } else {
            model.addAttribute("errorMessage", "Không tìm thấy nhà với ID: " + id);
            return "redirect:/house/list"; 
        }
    }

    @PostMapping("/edit")
    public String editHouse(@ModelAttribute("house") House house,
                            @RequestParam(value = "image", required = false) MultipartFile image,
                            @RequestParam(value = "imageUrl", required = false) String imageUrl,
                            @RequestParam(value = "houseId", required = false) Integer houseId,
                            Model model) {

        try {
            if (house.getHouseId() == 0 && houseId != null) {
                house.setHouseId(houseId);
            }

            if ((image == null || image.isEmpty()) && (imageUrl == null || imageUrl.trim().isEmpty())) {
                model.addAttribute("errorMessage", "Vui lòng tải lên ảnh hoặc nhập URL.");
                return "house/edit";
            }

            if ((image != null && !image.isEmpty()) && (imageUrl != null && !imageUrl.trim().isEmpty())) {
                model.addAttribute("errorMessage", "Chỉ được chọn một trong hai: tải lên ảnh hoặc nhập URL.");
                return "house/edit";
            }

            if (image != null && !image.isEmpty()) {
                byte[] imageBytes = image.getBytes();
                String base64Image = Base64.getEncoder().encodeToString(imageBytes);

                HouseImage houseImage = new HouseImage();
                houseImage.setImageUrl(base64Image);  
                houseImage.setHouse(house);
                houseImage.setMain(false);
                house.getHouseImages().add(houseImage); 
            }

            if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                if (imageUrl.startsWith("data:")) {
                    try {
                        String base64Image = imageUrl.split(",")[1];  
                        byte[] imageBytes = Base64.getDecoder().decode(base64Image);

                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        Thumbnails.of(new ByteArrayInputStream(imageBytes))
                                .size(800, 800)
                                .outputFormat("JPEG")
                                .outputQuality(0.8f)
                                .toOutputStream(outputStream);

                        byte[] resizedImage = outputStream.toByteArray();
                        String resizedBase64Image = Base64.getEncoder().encodeToString(resizedImage);

                        HouseImage houseImage = new HouseImage();
                        houseImage.setImageUrl(resizedBase64Image); 
                        houseImage.setHouse(house);
                        houseImage.setMain(true); 

                        house.getHouseImages().add(houseImage);
                    } catch (Exception e) {
                        model.addAttribute("errorMessage", "Đã xảy ra lỗi khi xử lý ảnh từ URL.");
                        return "house/edit";
                    }
                } else {
                    
                    try {
                        URL url = new URL(imageUrl);
                        InputStream inputStream = url.openStream();
                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

                        Thumbnails.of(inputStream)
                                .size(800, 800) 
                                .outputFormat("JPEG")
                                .outputQuality(0.8f)
                                .toOutputStream(outputStream);

                        byte[] resizedImage = outputStream.toByteArray();
                        String base64Image = Base64.getEncoder().encodeToString(resizedImage);

                        HouseImage houseImage = new HouseImage();
                        houseImage.setImageUrl(base64Image); 
                        houseImage.setHouse(house);
                        houseImage.setMain(false); 

                        house.getHouseImages().add(houseImage);
                    } catch (IOException e) {
                        model.addAttribute("errorMessage", "Không thể tải ảnh từ URL. Lỗi: " + e.getMessage());
                        return "house/edit";
                    }
                }
            }

            houseService.updateHouse(house, image);

            return "redirect:/house/edit/" + house.getHouseId();

        } catch (Exception e) {
            model.addAttribute("errorMessage", "Đã xảy ra lỗi: " + e.getMessage());
            return "house/edit";
        }
    }

    @GetMapping("/search")
    public String searchHouses(@RequestParam(required = false) String propertyName,
                               @RequestParam(required = false) String status,
                               Model model, Authentication authentication) {
        List<House> houses;
        String username = authentication.getName();
        User user = userService.getUserByUsername(username);
        model.addAttribute("user", user);

        if (propertyName != null && !propertyName.isEmpty()) {
            houses = houseService.searchHousesByNameAndUser(propertyName, user.getUserId());
        } else if (status != null && !status.isEmpty()) {
            houses = houseService.searchHousesByStatusAndUser(House.Status.valueOf(status.toUpperCase()), user.getUserId());
        } else {
            houses = houseService.getHousesByUserId(user.getUserId());
        }

        model.addAttribute("houses", houses);
        return "house/search";  
    }






}