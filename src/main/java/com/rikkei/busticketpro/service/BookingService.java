package com.rikkei.busticketpro.service;

import com.rikkei.busticketpro.dto.BookingRequestDTO;
import com.rikkei.busticketpro.entity.*;
import com.rikkei.busticketpro.repository.SeatRepository;
import com.rikkei.busticketpro.repository.TicketRepository;
import com.rikkei.busticketpro.repository.TripRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class BookingService {

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private TicketRepository ticketRepository;

    /**
     * CORE-06: Đặt vé với Pessimistic Lock — chống đặt trùng ghế.
     *
     * @param dto  thông tin đặt vé từ form
     * @param user người dùng đang đăng nhập (null nếu là khách vãng lai)
     * @return ticket_code của vé vừa tạo
     */
    @Transactional(rollbackFor = Exception.class)
    public String processBooking(BookingRequestDTO dto, User user) {
        // Lấy ghế với PESSIMISTIC_WRITE lock — SELECT FOR UPDATE
        Seat seat = seatRepository.findByIdWithLock(dto.getSeatId())
                .orElseThrow(() -> new RuntimeException("Ghế không tồn tại"));

        // Kiểm tra ghế còn trống
        if (seat.getStatus() != SeatStatus.AVAILABLE) {
            throw new RuntimeException("Ghế đã có người đặt, vui lòng chọn ghế khác");
        }

        Trip trip = tripRepository.findById(dto.getTripId())
                .orElseThrow(() -> new RuntimeException("Chuyến xe không tồn tại"));

        // Tạo vé
        Ticket ticket = new Ticket();
        ticket.setTicketCode(generateTicketCode());
        ticket.setCustomerName(dto.getCustomerName());
        ticket.setPhoneNumber(dto.getPhoneNumber());
        ticket.setEmail(dto.getEmail());
        ticket.setUser(user);           // null nếu khách vãng lai
        ticket.setTrip(trip);
        ticket.setSeat(seat);
        ticket.setTotalAmount(trip.getTicketPrice());
        ticket.setStatus(TicketStatus.PENDING);
        ticketRepository.save(ticket);

        // Cập nhật trạng thái ghế → PENDING
        seat.setStatus(SeatStatus.PENDING);
        seatRepository.save(seat);

        return ticket.getTicketCode();
    }

    private String generateTicketCode() {
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyMMddHHmmssSSS"));
        return "BTP" + timestamp;  // VD: BTP260520145532123 (18 ký tự ≤ 20)
    }
}
