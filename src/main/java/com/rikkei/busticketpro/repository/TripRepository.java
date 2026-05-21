package com.rikkei.busticketpro.repository;

import com.rikkei.busticketpro.entity.Trip;
import com.rikkei.busticketpro.entity.TripStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TripRepository extends JpaRepository<Trip, Long> {

    //Tìm chuyến theo tuyến đường + ngày khởi hành
    @Query("SELECT t FROM Trip t " +
           "JOIN FETCH t.route r " +
           "JOIN FETCH r.fromLocation " +
           "JOIN FETCH r.toLocation " +
           "JOIN FETCH t.bus " +
           "WHERE r.fromLocation.id = :fromId " +
           "  AND r.toLocation.id   = :toId " +
           "  AND t.departureTime  >= :from " +
           "  AND t.departureTime  <  :to " +
           "  AND t.status = 'READY'")
    List<Trip> searchTrips(@Param("fromId") Long fromId,
                           @Param("toId")   Long toId,
                           @Param("from")   LocalDateTime from,
                           @Param("to")     LocalDateTime to);

    //Tìm chuyến theo tuyến đường
    @Query("SELECT t FROM Trip t " +
           "JOIN FETCH t.route r " +
           "JOIN FETCH r.fromLocation " +
           "JOIN FETCH r.toLocation " +
           "JOIN FETCH t.bus " +
           "WHERE r.fromLocation.id = :fromId " +
           "  AND r.toLocation.id   = :toId " +
           "  AND t.status = 'READY' " +
           "ORDER BY t.departureTime ASC")
    List<Trip> searchTripsWithoutDate(@Param("fromId") Long fromId,
                                      @Param("toId")   Long toId);

    //Admin/Staff: danh sách chuyến theo trạng thái
    List<Trip> findByStatus(TripStatus status);

    //Lấy danh sách tất cả các chuyến xe có sẵn (READY) kèm eager load
    @Query("SELECT t FROM Trip t " +
           "JOIN FETCH t.route r " +
           "JOIN FETCH r.fromLocation " +
           "JOIN FETCH r.toLocation " +
           "JOIN FETCH t.bus " +
           "WHERE t.status = 'READY' " +
           "ORDER BY t.departureTime ASC")
    List<Trip> findAllReadyTrips();

    // Hướng 3: Tìm các chuyến xe đã chạy qua thời điểm hiện tại
    List<Trip> findByStatusAndDepartureTimeBefore(TripStatus status, LocalDateTime time);
}
