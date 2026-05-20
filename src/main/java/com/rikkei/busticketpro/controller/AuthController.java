package com.rikkei.busticketpro.controller;

import com.rikkei.busticketpro.dto.RegisterDTO;
import com.rikkei.busticketpro.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    // Trang đăng nhập
    @GetMapping("/login")
    public String loginPage() {
        return "passenger/login";
    }

    // Trang đăng ký
    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("registerDTO", new RegisterDTO());
        return "passenger/register";
    }

    //Xử lý đăng ký tài khoản mới
    @PostMapping("/register")
    public String register(@Valid @ModelAttribute RegisterDTO dto,
                           BindingResult result,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        // Trả về form nếu có lỗi validation
        if (result.hasErrors()) {
            return "passenger/register";
        }

        try {
            userService.register(dto);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Đăng ký thành công! Vui lòng đăng nhập.");
            return "redirect:/login";
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "passenger/register";
        }
    }
}
