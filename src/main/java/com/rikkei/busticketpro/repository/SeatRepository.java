package com.rikkei.busticketpro.repository;

import com.rikkei.busticketpro.entity.Seat;
import com.rikkei.busticketpro.entity.SeatStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    //Lấy tất cả ghế của một chuyến để vẽ sơ đồ
    List<Seat> findByTripIdOrderBySeatNumber(Long tripId);

    //Đếm ghế trống còn lại
    long countByTripIdAndStatus(Long tripId, SeatStatus status);

    //Lấy ghế với Pessimistic Lock chống đặt trùng
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Seat s WHERE s.id = :id")
    Optional<Seat> findByIdWithLock(@Param("id") Long id);
}
