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

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private BusService busService;

    @Autowired
    private TripService tripService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalBuses", busService.getAllBuses().size());
        model.addAttribute("totalTrips", tripService.getAllTrips().size());
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
