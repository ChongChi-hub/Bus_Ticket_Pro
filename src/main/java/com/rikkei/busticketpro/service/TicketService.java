package com.rikkei.busticketpro.service;

import com.rikkei.busticketpro.dto.TicketDetailDTO;
import com.rikkei.busticketpro.entity.Ticket;
import com.rikkei.busticketpro.entity.TicketStatus;
import com.rikkei.busticketpro.repository.TicketRepository;
import com.rikkei.busticketpro.entity.Seat;
import com.rikkei.busticketpro.entity.SeatStatus;
import com.rikkei.busticketpro.repository.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rikkei.busticketpro.dto.RevenueReportDTO;
import com.rikkei.busticketpro.dto.TopTripDTO;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private SeatRepository seatRepository;

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
     * VNPay: Cập nhật vé sang PAID sau khi thanh toán thành công.
     */
    @Transactional
    public Ticket markAsPaid(String ticketCode) {
        Ticket ticket = ticketRepository.findByTicketCode(ticketCode)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy vé: " + ticketCode));
        ticket.setStatus(TicketStatus.PAID);
        ticket.setPaidAt(LocalDateTime.now());

        Seat seat = ticket.getSeat();
        seat.setStatus(SeatStatus.BOOKED);
        seatRepository.save(seat);

        return ticketRepository.save(ticket);
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

    public Ticket markFindByCode(String ticketCode) {
        return ticketRepository.findByTicketCode(ticketCode)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy vé: " + ticketCode));
    }

    public List<RevenueReportDTO> getRevenueByMonth(int year) {
        return ticketRepository.getRevenueByMonth(year);
    }

    public List<TopTripDTO> getTopTrips(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return ticketRepository.getTopTrips(pageable);
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
