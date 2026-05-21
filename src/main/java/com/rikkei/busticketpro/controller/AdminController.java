package com.rikkei.busticketpro.controller;

import com.rikkei.busticketpro.dto.BusDTO;
import com.rikkei.busticketpro.entity.Bus;
import com.rikkei.busticketpro.entity.BusType;
import com.rikkei.busticketpro.entity.Status;
import com.rikkei.busticketpro.service.BusService;
import com.rikkei.busticketpro.service.TripService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.rikkei.busticketpro.dto.RevenueReportDTO;
import com.rikkei.busticketpro.dto.TopTripDTO;
import com.rikkei.busticketpro.service.TicketService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private BusService busService;

    @Autowired
    private TripService tripService;

    @Autowired
    private TicketService ticketService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalBuses", busService.getAllBuses().size());
        model.addAttribute("totalTrips", tripService.getAllTrips().size());

        // Báo cáo doanh thu va Top chuyến xe
        int currentYear = LocalDate.now().getYear();
        List<RevenueReportDTO> revenueData = ticketService.getRevenueByMonth(currentYear);
        
        // chartjs
        BigDecimal[] monthlyRevenues = new BigDecimal[12];
        for (int i = 0; i < 12; i++) monthlyRevenues[i] = BigDecimal.ZERO;
        
        BigDecimal totalRevenue = BigDecimal.ZERO;
        for (RevenueReportDTO dto : revenueData) {
            int monthIndex = dto.getMonth() - 1;
            monthlyRevenues[monthIndex] = dto.getRevenue();
            totalRevenue = totalRevenue.add(dto.getRevenue());
        }

        List<TopTripDTO> topTrips = ticketService.getTopTrips(5);

        model.addAttribute("currentYear", currentYear);
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("monthlyRevenues", monthlyRevenues);
        model.addAttribute("topTrips", topTrips);

        return "admin/dashboard";
    }

    // Quản lý xe
    @GetMapping("/buses")
    public String busList(Model model) {
        model.addAttribute("buses", busService.getAllBuses());
        return "admin/bus-list";
    }

    @GetMapping("/buses/new")
    public String busCreateForm(Model model) {
        model.addAttribute("busDTO", new BusDTO());
        model.addAttribute("busTypes", BusType.values());
        model.addAttribute("statuses", Status.values());
        return "admin/bus-form";
    }

    @PostMapping("/buses")
    public String createBus(@Valid @ModelAttribute BusDTO busDTO,
                            BindingResult result,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("busTypes", BusType.values());
            model.addAttribute("statuses", Status.values());
            return "admin/bus-form";
        }
        try {
            busService.createBus(busDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Thêm xe thành công!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/buses";
    }

    @GetMapping("/buses/{id}/edit")
    public String busEditForm(@PathVariable Long id, Model model) {
        Bus bus = busService.findById(id);

        BusDTO dto = new BusDTO();
        dto.setId(bus.getId());
        dto.setPlateNumber(bus.getPlateNumber());
        dto.setBusType(bus.getBusType());
        dto.setTotalSeats(bus.getTotalSeats());
        dto.setBrand(bus.getBrand());
        dto.setDriverName(bus.getDriverName());
        dto.setStatus(bus.getStatus());

        model.addAttribute("busDTO", dto);
        model.addAttribute("busTypes", BusType.values());
        model.addAttribute("statuses", Status.values());
        return "admin/bus-form";
    }

    @PostMapping("/buses/{id}/update")
    public String updateBus(@PathVariable Long id,
                            @Valid @ModelAttribute BusDTO busDTO,
                            BindingResult result,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("busTypes", BusType.values());
            model.addAttribute("statuses", Status.values());
            return "admin/bus-form";
        }
        try {
            busService.updateBus(id, busDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật xe thành công!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/buses";
    }

    @PostMapping("/buses/{id}/delete")
    public String deleteBus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            busService.deleteBus(id);
            redirectAttributes.addFlashAttribute("successMessage", "Đã vô hiệu hóa xe!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/buses";
    }
}
