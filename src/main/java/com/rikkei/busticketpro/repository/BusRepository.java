package com.rikkei.busticketpro.repository;

import com.rikkei.busticketpro.entity.Bus;
import com.rikkei.busticketpro.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BusRepository extends JpaRepository<Bus, Long> {

    // Lấy danh sách xe theo trạng thái
    List<Bus> findByStatus(Status status);

    boolean existsByPlateNumber(String plateNumber);
}
