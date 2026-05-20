package com.rikkei.busticketpro.controller;

import com.rikkei.busticketpro.dto.TripDTO;
import com.rikkei.busticketpro.entity.Status;
import com.rikkei.busticketpro.entity.Trip;
import com.rikkei.busticketpro.entity.TripStatus;
import com.rikkei.busticketpro.repository.BusRepository;
import com.rikkei.busticketpro.repository.RouteRepository;
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
public class AdminTripController {

    @Autowired
    private TripService tripService;

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private BusRepository busRepository;

    @GetMapping("/trips")
    public String tripList(Model model) {
        model.addAttribute("trips", tripService.getAllTrips());
        return "admin/trip-list";
    }

    @GetMapping("/trips/new")
    public String tripCreateForm(Model model) {
        model.addAttribute("tripDTO", new TripDTO());
        model.addAttribute("routes", routeRepository.findAll());
        model.addAttribute("buses", busRepository.findByStatus(Status.ACTIVE));
        model.addAttribute("statuses", TripStatus.values());
        return "admin/trip-form";
    }

    @PostMapping("/trips")
    public String createTrip(@Valid @ModelAttribute("tripDTO") TripDTO tripDTO,
                             BindingResult result,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("routes", routeRepository.findAll());
            model.addAttribute("buses", busRepository.findByStatus(Status.ACTIVE));
            model.addAttribute("statuses", TripStatus.values());
            return "admin/trip-form";
        }
        try {
            tripService.createTrip(tripDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Thêm chuyến xe mới và tạo sơ đồ ghế thành công!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            model.addAttribute("routes", routeRepository.findAll());
            model.addAttribute("buses", busRepository.findByStatus(Status.ACTIVE));
            model.addAttribute("statuses", TripStatus.values());
            return "admin/trip-form";
        }
        return "redirect:/admin/trips";
    }

    @GetMapping("/trips/{id}/edit")
    public String tripEditForm(@PathVariable Long id, Model model) {
        Trip trip = tripService.findById(id);

        TripDTO dto = new TripDTO();
        dto.setId(trip.getId());
        dto.setRouteId(trip.getRoute().getId());
        dto.setBusId(trip.getBus().getId());
        dto.setDepartureTime(trip.getDepartureTime());
        dto.setArrivalTime(trip.getArrivalTime());
        dto.setTicketPrice(trip.getTicketPrice());
        dto.setStatus(trip.getStatus());

        model.addAttribute("tripDTO", dto);
        model.addAttribute("routes", routeRepository.findAll());
        model.addAttribute("buses", busRepository.findByStatus(Status.ACTIVE));
        model.addAttribute("statuses", TripStatus.values());
        return "admin/trip-form";
    }

    @PostMapping("/trips/{id}/update")
    public String updateTrip(@PathVariable Long id,
                             @Valid @ModelAttribute("tripDTO") TripDTO tripDTO,
                             BindingResult result,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("routes", routeRepository.findAll());
            model.addAttribute("buses", busRepository.findByStatus(Status.ACTIVE));
            model.addAttribute("statuses", TripStatus.values());
            return "admin/trip-form";
        }
        try {
            tripService.updateTrip(id, tripDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật chuyến xe thành công!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            model.addAttribute("routes", routeRepository.findAll());
            model.addAttribute("buses", busRepository.findByStatus(Status.ACTIVE));
            model.addAttribute("statuses", TripStatus.values());
            return "admin/trip-form";
        }
        return "redirect:/admin/trips";
    }

    @PostMapping("/trips/{id}/delete")
    public String deleteTrip(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            tripService.deleteTrip(id);
            redirectAttributes.addFlashAttribute("successMessage", "Đã hủy chuyến xe!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/trips";
    }
}
