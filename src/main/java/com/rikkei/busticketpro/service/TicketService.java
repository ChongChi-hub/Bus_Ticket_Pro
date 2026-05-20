package com.rikkei.busticketpro.service;

import com.rikkei.busticketpro.dto.TicketDetailDTO;
import com.rikkei.busticketpro.entity.Ticket;
import com.rikkei.busticketpro.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    /**
     * CORE-07: Tra cứu vé chi tiết bằng mã vé + số điện thoại.
     */
    public TicketDetailDTO getTicketDetail(String code, String phone) {
        Ticket ticket = ticketRepository
                .findByTicketCodeAndPhoneNumber(code, phone)
                .orElseThrow(() -> new RuntimeException(
                        "Không tìm thấy vé với mã và số điện thoại đã cung cấp"));
        return toDTO(ticket);
    }

    /**
     * Lấy danh sách vé của hành khách đã đăng nhập.
     */
    public List<Ticket> getMyTickets(Long userId) {
        return ticketRepository.findByUserIdOrderByBookingTimeDesc(userId);
    }

    public Ticket findById(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy vé"));
    }

    private TicketDetailDTO toDTO(Ticket t) {
        return TicketDetailDTO.builder()
                .ticketId(t.getId())
                .ticketCode(t.getTicketCode())
                .customerName(t.getCustomerName())
                .phoneNumber(t.getPhoneNumber())
                .email(t.getEmail())
                .seatNumber(t.getSeat().getSeatNumber())
                .fromLocation(t.getTrip().getRoute().getFromLocation().getName())
                .toLocation(t.getTrip().getRoute().getToLocation().getName())
                .distanceKm(t.getTrip().getRoute().getDistanceKm())
                .departureTime(t.getTrip().getDepartureTime())
                .arrivalTime(t.getTrip().getArrivalTime())
                .plateNumber(t.getTrip().getBus().getPlateNumber())
                .busType(t.getTrip().getBus().getBusType().name())
                .driverName(t.getTrip().getBus().getDriverName())
                .totalAmount(t.getTotalAmount())
                .status(t.getStatus())
                .bookingTime(t.getBookingTime())
                .paidAt(t.getPaidAt())
                .build();
    }
}
