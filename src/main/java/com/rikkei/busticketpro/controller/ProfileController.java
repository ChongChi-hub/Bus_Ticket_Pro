package com.rikkei.busticketpro.controller;

import com.rikkei.busticketpro.dto.ProfileDTO;
import com.rikkei.busticketpro.entity.User;
import com.rikkei.busticketpro.entity.UserProfile;
import com.rikkei.busticketpro.security.CustomUserDetails;
import com.rikkei.busticketpro.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private UserService userService;

    @GetMapping
    public String showProfile(Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        User user = userDetails.getUser();
        UserProfile profile = userService.getProfile(user.getId());

        ProfileDTO dto = new ProfileDTO();
        dto.setFullName(profile.getFullName());
        dto.setPhoneNumber(profile.getPhoneNumber());
        dto.setEmail(profile.getEmail());
        dto.setAddress(profile.getAddress());

        model.addAttribute("profileDTO", dto);
        model.addAttribute("username", user.getUsername());
        model.addAttribute("role", user.getRole().name());

        return "passenger/profile";
    }

    @PostMapping("/update")
    public String updateProfile(@Valid @ModelAttribute("profileDTO") ProfileDTO dto,
                                BindingResult result,
                                Model model,
                                @AuthenticationPrincipal CustomUserDetails userDetails,
                                RedirectAttributes redirectAttributes) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        User user = userDetails.getUser();

        if (result.hasErrors()) {
            model.addAttribute("username", user.getUsername());
            model.addAttribute("role", user.getRole().name());
            return "passenger/profile";
        }

        try {
            UserProfile updatedProfile = new UserProfile();
            updatedProfile.setFullName(dto.getFullName());
            updatedProfile.setPhoneNumber(dto.getPhoneNumber());
            updatedProfile.setEmail(dto.getEmail());
            updatedProfile.setAddress(dto.getAddress());

            userService.updateProfile(user.getId(), updatedProfile);

            // Cập nhật UserProfile trong đối tượng CustomUserDetails hiện tại để đồng bộ session
            if (user.getUserProfile() != null) {
                user.getUserProfile().setFullName(dto.getFullName());
                user.getUserProfile().setPhoneNumber(dto.getPhoneNumber());
                user.getUserProfile().setEmail(dto.getEmail());
                user.getUserProfile().setAddress(dto.getAddress());
            }

            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật hồ sơ cá nhân thành công!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/profile";
    }
}
