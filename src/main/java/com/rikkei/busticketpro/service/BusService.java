package com.rikkei.busticketpro.service;

import com.rikkei.busticketpro.dto.BusDTO;
import com.rikkei.busticketpro.entity.Bus;
import com.rikkei.busticketpro.entity.Status;
import com.rikkei.busticketpro.repository.BusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BusService {

    @Autowired
    private BusRepository busRepository;

    /** Lấy danh sách tất cả xe. */
    public List<Bus> getAllBuses() {
        return busRepository.findAll();
    }

    public Bus findById(Long id) {
        return busRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy xe"));
    }

    /** Thêm xe mới. */
    @Transactional
    public Bus createBus(BusDTO dto) {
        if (busRepository.existsByPlateNumber(dto.getPlateNumber())) {
            throw new RuntimeException("Biển số xe đã tồn tại: " + dto.getPlateNumber());
        }

        Bus bus = new Bus();
        bus.setPlateNumber(dto.getPlateNumber());
        bus.setBusType(dto.getBusType());
        bus.setTotalSeats(dto.getTotalSeats());
        bus.setBrand(dto.getBrand());
        bus.setDriverName(dto.getDriverName());
        bus.setStatus(dto.getStatus() != null ? dto.getStatus() : Status.ACTIVE);
        return busRepository.save(bus);
    }

    /** Cập nhật thông tin xe. */
    @Transactional
    public Bus updateBus(Long id, BusDTO dto) {
        Bus bus = findById(id);
        bus.setPlateNumber(dto.getPlateNumber());
        bus.setBusType(dto.getBusType());
        // Giữ cố định số ghế khi đã tạo xe mới
        bus.setBrand(dto.getBrand());
        bus.setDriverName(dto.getDriverName());
        bus.setStatus(dto.getStatus());
        return busRepository.save(bus);
    }

    /** Xóa (soft delete — đặt status = INACTIVE). */
    @Transactional
    public void deleteBus(Long id) {
        Bus bus = findById(id);
        bus.setStatus(Status.INACTIVE);
        busRepository.save(bus);
    }
}
