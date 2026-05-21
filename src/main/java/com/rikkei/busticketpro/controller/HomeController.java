package com.rikkei.busticketpro.controller;

import com.rikkei.busticketpro.dto.TripSearchDTO;
import com.rikkei.busticketpro.repository.LocationRepository;
import com.rikkei.busticketpro.service.TripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public class HomeController {

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private TripService tripService;

    // Trang chủ — hiển thị form tìm kiếm chuyến xe và tất cả chuyến xe khả dụng
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("locations", locationRepository.findAll());
        model.addAttribute("searchDTO", new TripSearchDTO());
        model.addAttribute("trips", tripService.getAllReadyTripResults());
        return "passenger/index";
    }

    // Xử lý tìm kiếm — hiển thị danh sách chuyến phù hợp
    @GetMapping("/search")
    public String search(@ModelAttribute TripSearchDTO searchDTO, Model model) {
        model.addAttribute("locations", locationRepository.findAll());
        model.addAttribute("searchDTO", searchDTO);

        if (searchDTO.getFromLocationId() != null && searchDTO.getToLocationId() != null) {
            model.addAttribute("trips", tripService.searchTrips(searchDTO));
        }
        return "passenger/search-results";
    }

    /** Trang lỗi 403 */
    @GetMapping("/access-denied")
    public String accessDenied() {
        return "error/access-denied";
    }
}
