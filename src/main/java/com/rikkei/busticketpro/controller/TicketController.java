package com.rikkei.busticketpro.controller;

import com.rikkei.busticketpro.entity.Seat;
import com.rikkei.busticketpro.entity.Trip;
import com.rikkei.busticketpro.entity.Ticket;
import com.rikkei.busticketpro.dto.BookingRequestDTO;
import com.rikkei.busticketpro.entity.User;
import com.rikkei.busticketpro.security.CustomUserDetails;
import com.rikkei.busticketpro.service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class TicketController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private TicketService ticketService;

    @Autowired
    private CancelService cancelService;

    @Autowired
    private TripService tripService;

    @Autowired
    private SeatService seatService;

    // Trang xác nhận đặt vé sau khi chọn ghế
    @GetMapping("/book-ticket")
    public String bookingConfirmPage(@RequestParam Long tripId,
                                     @RequestParam Long seatId,
                                     Model model,
                                     @AuthenticationPrincipal CustomUserDetails userDetails) {
        Trip trip = tripService.findById(tripId);
        Seat seat = seatService.findById(seatId);

        model.addAttribute("trip", trip);
        model.addAttribute("seat", seat);

        BookingRequestDTO dto = new BookingRequestDTO();
        dto.setTripId(tripId);
        dto.setSeatId(seatId);

        // Điền sẵn thông tin nếu đã đăng nhập
        if (userDetails != null) {
            User u = userDetails.getUser();
            if (u.getUserProfile() != null) {
                dto.setCustomerName(u.getUserProfile().getFullName());
                dto.setPhoneNumber(u.getUserProfile().getPhoneNumber());
                dto.setEmail(u.getUserProfile().getEmail());
            }
        }

        model.addAttribute("bookingForm", dto);
        return "passenger/booking-form";
    }

    // xử lý đặt vé 
    @PostMapping("/book-ticket")
    public String processBooking(@Valid @ModelAttribute("bookingForm") BookingRequestDTO dto,
                                 BindingResult result,
                                 Model model,
                                 HttpServletRequest request,
                                 @AuthenticationPrincipal CustomUserDetails userDetails,
                                 RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("trip", tripService.findById(dto.getTripId()));
            model.addAttribute("seat", seatService.findById(dto.getSeatId()));
            return "passenger/booking-form";
        }

        try {
            User user = userDetails != null ? userDetails.getUser() : null;
            String ticketCode = bookingService.processBooking(dto, user);
            
            // Redirect sang endpoint tạo payment link (PayOS)
            return "redirect:/payment/pay-now?ticketCode=" + ticketCode + "&phone=" + dto.getPhoneNumber();
        } catch (RuntimeException e) {
            model.addAttribute("trip", tripService.findById(dto.getTripId()));
            model.addAttribute("seat", seatService.findById(dto.getSeatId()));
            model.addAttribute("errorMessage", e.getMessage());
            return "passenger/booking-form";
        }
    }

    // tra cứu vé bằng mã + sdt
    @GetMapping("/tickets/lookup")
    public String ticketLookupPage() {
        return "passenger/ticket-lookup";
    }

    // chi tiết vé 
    @GetMapping("/tickets/{code}")
    public String ticketDetail(@PathVariable String code,
                               @RequestParam String phone,
                               @RequestParam(required = false) String paymentMessage,
                               Model model) {
        try {
            if ("success".equals(paymentMessage)) {
                model.addAttribute("successMessage", "Thanh toán thành công! Cảm ơn quý khách");
            } else if ("failed".equals(paymentMessage)) {
                model.addAttribute("errorMessage", "Giao dịch chưa hoàn tất.");
            } else if ("cancel".equals(paymentMessage)) {
                model.addAttribute("errorMessage", "Bạn đã hủy quá trình thanh toán.");
            } else if ("error".equals(paymentMessage)) {
                model.addAttribute("errorMessage", "Đã xảy ra lỗi trong quá trình xác thực thanh toán.");
            }

            model.addAttribute("ticket", ticketService.getTicketDetail(code, phone));
            return "passenger/ticket-detail";
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "passenger/ticket-lookup";
        }
    }


    // xử lý huỷ vé từ khách
    @PostMapping("/cancel-ticket")
    public String cancelTicket(@RequestParam Long ticketId,
                               @RequestParam String phoneNumber,
                               RedirectAttributes redirectAttributes) {
        try {
            cancelService.cancelProcess(ticketId, phoneNumber);
            redirectAttributes.addFlashAttribute("successMessage", "Hủy vé thành công!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/tickets/lookup";
    }

    // lịch sử vé 
    @GetMapping("/my-tickets")
    public String myTickets(@AuthenticationPrincipal CustomUserDetails userDetails,
                            Model model) {
        model.addAttribute("tickets",
                ticketService.getMyTickets(userDetails.getUser().getId()));
        return "passenger/my-tickets";
    }
}
