package com.rikkei.busticketpro.service;

import com.rikkei.busticketpro.entity.*;
import com.rikkei.busticketpro.repository.SeatRepository;
import com.rikkei.busticketpro.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class CancelService {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private SeatRepository seatRepository;

    /**
     * Hành khách hủy vé.
     * Điều kiện:
     *   - Vé ở trạng thái PENDING (chưa thanh toán)
     *   - Thời gian hiện tại còn trước giờ khởi hành ít nhất 12 tiếng
     */
    @Transactional
    public void cancelProcess(Long ticketId, String phoneNumber) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy vé"));

        // Xác minh số điện thoại khớp
        if (!ticket.getPhoneNumber().equals(phoneNumber)) {
            throw new RuntimeException("Số điện thoại không khớp với vé này");
        }

        // Không thể hủy vé đã thanh toán
        if (ticket.getStatus() == TicketStatus.PAID) {
            throw new RuntimeException("Không thể hủy vé đã thanh toán");
        }

        // Không thể hủy vé đã bị hủy trước đó
        if (ticket.getStatus() == TicketStatus.CANCELLED) {
            throw new RuntimeException("Vé này đã được hủy trước đó");
        }

        // Kiểm tra quy tắc 12 tiếng
        LocalDateTime departure = ticket.getTrip().getDepartureTime();
        if (LocalDateTime.now().plusHours(12).isAfter(departure)) {
            throw new RuntimeException(
                    "Không thể hủy vé khi còn dưới 12 tiếng trước giờ khởi hành");
        }

        // Thực hiện hủy vé
        ticket.setStatus(TicketStatus.CANCELLED);
        ticketRepository.save(ticket);

        // Giải phóng ghế
        Seat seat = ticket.getSeat();
        seat.setStatus(SeatStatus.AVAILABLE);
        seatRepository.save(seat);
    }
}
