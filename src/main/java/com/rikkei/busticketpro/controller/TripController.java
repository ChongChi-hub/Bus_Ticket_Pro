package com.rikkei.busticketpro.controller;

import com.rikkei.busticketpro.dto.BookingRequestDTO;
import com.rikkei.busticketpro.dto.SeatDTO;
import com.rikkei.busticketpro.entity.Trip;
import com.rikkei.busticketpro.entity.User;
import com.rikkei.busticketpro.security.CustomUserDetails;
import com.rikkei.busticketpro.service.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class TripController {

    @Autowired
    private TripService tripService;

    @Autowired
    private SeatService seatService;

    // hiển thị sơ đồ ghế của một chuyến
    @GetMapping("/trips/{tripId}/seats")
    public String seatMapPage(@PathVariable Long tripId, Model model) {
        Trip trip = tripService.findById(tripId);
        model.addAttribute("trip", trip);
        model.addAttribute("bookingForm", new BookingRequestDTO());
        return "passenger/seat-map";
    }

    //REST API — trả về JSON danh sách ghế (dùng cho AJAX)
    @GetMapping("/api/seats")
    @ResponseBody
    public ResponseEntity<List<SeatDTO>> getSeats(@RequestParam Long tripId) {
        return ResponseEntity.ok(seatService.getSeatMap(tripId));
    }
}
