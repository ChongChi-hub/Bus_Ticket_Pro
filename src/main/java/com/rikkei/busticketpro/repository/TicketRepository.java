package com.rikkei.busticketpro.repository;

import com.rikkei.busticketpro.entity.Ticket;
import com.rikkei.busticketpro.entity.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

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
}
