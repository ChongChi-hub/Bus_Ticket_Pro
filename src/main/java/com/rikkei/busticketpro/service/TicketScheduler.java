package com.rikkei.busticketpro.service;

import com.rikkei.busticketpro.entity.Ticket;
import com.rikkei.busticketpro.entity.TicketStatus;
import com.rikkei.busticketpro.entity.Trip;
import com.rikkei.busticketpro.entity.TripStatus;
import com.rikkei.busticketpro.repository.TicketRepository;
import com.rikkei.busticketpro.repository.TripRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class TicketScheduler {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private TripRepository tripRepository;

    /**
     * Chạy mỗi 10 phút.
     * Quét các vé PENDING được tạo cách đây hơn 30 phút và tự động HỦY.
     */
    @Scheduled(cron = "0 0/10 * * * *")
    @Transactional
    public void cancelExpiredPendingTickets() {
        LocalDateTime thirtyMinutesAgo = LocalDateTime.now().minusMinutes(30);
        List<Ticket> expiredPendingTickets = ticketRepository.findByStatusAndBookingTimeBefore(TicketStatus.PENDING, thirtyMinutesAgo);

        if (!expiredPendingTickets.isEmpty()) {
            for (Ticket ticket : expiredPendingTickets) {
                ticket.setStatus(TicketStatus.CANCELLED);
                System.out.println("🔄 Scheduler: Tự động hủy vé rác/quá hạn: " + ticket.getTicketCode());
            }
            ticketRepository.saveAll(expiredPendingTickets);
        }
    }

    /**
     * Chạy mỗi 1 giờ.
     * Quét các chuyến xe (Trip) có giờ khởi hành nằm trong quá khứ nhưng vẫn đang ở trạng thái READY/OPEN
     * Chuyển trạng thái sang COMPLETED (đã hoàn thành).
     */
    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void updateDepartedTrips() {
        LocalDateTime now = LocalDateTime.now();
        List<Trip> departedTrips = tripRepository.findByStatusAndDepartureTimeBefore(TripStatus.READY, now);

        if (!departedTrips.isEmpty()) {
            for (Trip trip : departedTrips) {
                trip.setStatus(TripStatus.COMPLETED);
                System.out.println("🔄 Scheduler: Cập nhật chuyến xe đã khởi hành: " + trip.getId());
            }
            tripRepository.saveAll(departedTrips);
        }
    }
}
