package com.rikkei.busticketpro.controller;

import com.rikkei.busticketpro.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/staff")
public class StaffController {

    @Autowired
    private PaymentService paymentService;

    // Dashboard nhân viên
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("pendingCount",
                paymentService.getPendingTickets().size());
        return "staff/dashboard";
    }

    // Danh sách vé đang chờ thanh toán 
    @GetMapping("/pending-tickets")
    public String pendingTickets(Model model) {
        model.addAttribute("tickets", paymentService.getPendingTickets());
        return "staff/pending-tickets";
    }

    // xác nhận thanh toán vé
    @PostMapping("/confirm-payment/{ticketId}")
    public String confirmPayment(@PathVariable Long ticketId,
                                 RedirectAttributes redirectAttributes) {
        try {
            paymentService.confirmPayment(ticketId);
            redirectAttributes.addFlashAttribute("successMessage", "Đã xác nhận thanh toán!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/staff/pending-tickets";
    }

    // huỷ vé quá hạn
    @PostMapping("/cancel-ticket/{ticketId}")
    public String cancelTicket(@PathVariable Long ticketId,
                               RedirectAttributes redirectAttributes) {
        try {
            paymentService.cancelByStaff(ticketId);
            redirectAttributes.addFlashAttribute("successMessage", "Đã hủy vé thành công!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/staff/pending-tickets";
    }
}
