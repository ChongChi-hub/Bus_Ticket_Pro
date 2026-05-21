package com.rikkei.busticketpro.repository;

import com.rikkei.busticketpro.entity.Ticket;
import com.rikkei.busticketpro.entity.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import com.rikkei.busticketpro.dto.RevenueReportDTO;
import com.rikkei.busticketpro.dto.TopTripDTO;
import org.springframework.data.domain.Pageable;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    //Tra cứu vé theo mã + SĐT
    @Query("SELECT t FROM Ticket t " +
           "JOIN FETCH t.seat s " +
           "JOIN FETCH t.trip tr " +
           "JOIN FETCH tr.route r " +
           "JOIN FETCH r.fromLocation " +
           "JOIN FETCH r.toLocation " +
           "JOIN FETCH tr.bus " +
           "WHERE t.ticketCode = :code AND t.phoneNumber = :phone")
    Optional<Ticket> findByTicketCodeAndPhoneNumber(@Param("code")  String code,
                                                    @Param("phone") String phone);

    //Staff xem danh sách vé chờ thanh toán
    @Query("SELECT t FROM Ticket t " +
           "JOIN FETCH t.seat s " +
           "JOIN FETCH t.trip tr " +
           "JOIN FETCH tr.route r " +
           "JOIN FETCH r.fromLocation " +
           "JOIN FETCH r.toLocation " +
           "WHERE t.status = :status " +
           "ORDER BY t.bookingTime ASC")
    List<Ticket> findByStatusWithDetails(@Param("status") TicketStatus status);

    //Lịch sử vé của một hành khách đã đăng nhập
    List<Ticket> findByUserIdOrderByBookingTimeDesc(Long userId);

    //Kiểm tra vé có tồn tại theo seat + trip (chống đặt trùng ở mức DB)
    boolean existsBySeatIdAndStatusNot(Long seatId, TicketStatus status);

    // VNPay/PayOS: tìm vé theo mã vé (không cần phone)
    Optional<Ticket> findByTicketCode(String ticketCode);

    // Hướng 4: Báo cáo thống kê doanh thu theo tháng trong năm
    @Query("SELECT new com.rikkei.busticketpro.dto.RevenueReportDTO(MONTH(t.paidAt), SUM(t.totalAmount)) " +
           "FROM Ticket t " +
           "WHERE t.status = 'PAID' AND YEAR(t.paidAt) = :year " +
           "GROUP BY MONTH(t.paidAt) " +
           "ORDER BY MONTH(t.paidAt) ASC")
    List<RevenueReportDTO> getRevenueByMonth(@Param("year") int year);

    // Hướng 4: Top 5 chuyến xe có lượt đặt vé cao nhất
    @Query("SELECT new com.rikkei.busticketpro.dto.TopTripDTO(t.trip, COUNT(t)) " +
           "FROM Ticket t " +
           "WHERE t.status = 'PAID' " +
           "GROUP BY t.trip " +
           "ORDER BY COUNT(t) DESC")
    List<TopTripDTO> getTopTrips(Pageable pageable);

    // Hướng 3: Lấy các vé PENDING đã quá hạn
    List<Ticket> findByStatusAndBookingTimeBefore(TicketStatus status, LocalDateTime time);
}
