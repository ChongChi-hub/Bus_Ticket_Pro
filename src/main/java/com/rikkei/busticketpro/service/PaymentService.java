package com.rikkei.busticketpro.service;

import com.rikkei.busticketpro.entity.*;
import com.rikkei.busticketpro.repository.SeatRepository;
import com.rikkei.busticketpro.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentService {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private SeatRepository seatRepository;

    /**
     * CORE-08: Lấy danh sách vé đang chờ thanh toán (Staff).
     */
    public List<Ticket> getPendingTickets() {
        return ticketRepository.findByStatusWithDetails(TicketStatus.PENDING);
    }

    /**
     * CORE-08: Staff xác nhận thanh toán vé.
     * PENDING → PAID, ghế → BOOKED
     */
    @Transactional
    public void confirmPayment(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy vé"));

        if (ticket.getStatus() != TicketStatus.PENDING) {
            throw new RuntimeException("Vé đã được xử lý (trạng thái: " + ticket.getStatus() + ")");
        }

        ticket.setStatus(TicketStatus.PAID);
        ticket.setPaidAt(LocalDateTime.now());
        ticketRepository.save(ticket);

        Seat seat = ticket.getSeat();
        seat.setStatus(SeatStatus.BOOKED);
        seatRepository.save(seat);
    }

    /**
     * CORE-08: Staff hủy vé quá hạn chưa thanh toán.
     * PENDING → CANCELLED, ghế → AVAILABLE
     */
    @Transactional
    public void cancelByStaff(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy vé"));

        if (ticket.getStatus() != TicketStatus.PENDING) {
            throw new RuntimeException("Chỉ có thể hủy vé đang ở trạng thái PENDING");
        }

        ticket.setStatus(TicketStatus.CANCELLED);
        ticketRepository.save(ticket);

        Seat seat = ticket.getSeat();
        seat.setStatus(SeatStatus.AVAILABLE);
        seatRepository.save(seat);
    }
}
