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

    //Admin/Staff: danh sách chuyến theo trạng thái
    List<Trip> findByStatus(TripStatus status);
}
