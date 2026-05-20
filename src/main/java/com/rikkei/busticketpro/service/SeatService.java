package com.rikkei.busticketpro.service;

import com.rikkei.busticketpro.dto.SeatDTO;
import com.rikkei.busticketpro.entity.Seat;
import com.rikkei.busticketpro.repository.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SeatService {

    @Autowired
    private SeatRepository seatRepository;

    public List<SeatDTO> getSeatMap(Long tripId) {
        return seatRepository.findByTripIdOrderBySeatNumber(tripId)
                .stream()
                .map(seat -> new SeatDTO(seat.getId(), seat.getSeatNumber(), seat.getStatus()))
                .collect(Collectors.toList());
    }

    public Seat findById(Long id) {
        return seatRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ghế"));
    }
}
