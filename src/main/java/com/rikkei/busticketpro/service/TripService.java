package com.rikkei.busticketpro.service;

import com.rikkei.busticketpro.dto.TripDTO;
import com.rikkei.busticketpro.dto.TripResultDTO;
import com.rikkei.busticketpro.dto.TripSearchDTO;
import com.rikkei.busticketpro.entity.*;
import com.rikkei.busticketpro.repository.BusRepository;
import com.rikkei.busticketpro.repository.RouteRepository;
import com.rikkei.busticketpro.repository.SeatRepository;
import com.rikkei.busticketpro.repository.TripRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TripService {

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private BusRepository busRepository;

    /**
     * CORE-05: Tra cứu chuyến xe theo tuyến đường + ngày.
     */
    public List<TripResultDTO> searchTrips(TripSearchDTO dto) {
        LocalDateTime from = dto.getDepartureDate().atStartOfDay();
        LocalDateTime to   = from.plusDays(1);

        List<Trip> trips = tripRepository.searchTrips(
                dto.getFromLocationId(), dto.getToLocationId(), from, to);

        return trips.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<Trip> getAllTrips() {
        return tripRepository.findAll();
    }

    public Trip findById(Long id) {
        return tripRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chuyến xe"));
    }

    /**
     * Thêm chuyến xe mới & tự động sinh sơ đồ ghế.
     */
    @Transactional
    public Trip createTrip(TripDTO dto) {
        Trip trip = new Trip();
        trip.setRoute(routeRepository.findById(dto.getRouteId())
                .orElseThrow(() -> new RuntimeException("Tuyến đường không tồn tại")));
        Bus bus = busRepository.findById(dto.getBusId())
                .orElseThrow(() -> new RuntimeException("Xe không tồn tại"));

        trip.setBus(bus);
        trip.setDepartureTime(dto.getDepartureTime());
        trip.setArrivalTime(dto.getArrivalTime());
        trip.setTicketPrice(dto.getTicketPrice());
        trip.setStatus(dto.getStatus() != null ? dto.getStatus() : TripStatus.READY);

        Trip savedTrip = tripRepository.save(trip);

        // Sinh ghế tự động dựa trên bus.totalSeats và bus.busType
        int totalSeats = bus.getTotalSeats();
        BusType busType = bus.getBusType();

        for (int i = 1; i <= totalSeats; i++) {
            Seat seat = new Seat();
            seat.setTrip(savedTrip);
            seat.setStatus(SeatStatus.AVAILABLE);

            String seatNumber;
            if (busType == BusType.SEAT) {
                // Ghế ngồi - 5 ghế mỗi hàng (A, B, C, D, E)
                int row = (i - 1) / 5 + 1;
                char letter = (char) ('A' + (i - 1) % 5);
                seatNumber = row + String.valueOf(letter);
            } else if (busType == BusType.SLEEPER) {
                // Giường nằm - 2 tầng (Dưới D, Trên T)
                int seatIndex = (i + 1) / 2;
                String suffix = (i % 2 != 0) ? "D" : "T";
                seatNumber = seatIndex + suffix;
            } else { // LIMOUSINE
                // Limousine - 2 ghế mỗi hàng (A, B)
                int row = (i - 1) / 2 + 1;
                char letter = (char) ('A' + (i - 1) % 2);
                seatNumber = row + String.valueOf(letter);
            }
            seat.setSeatNumber(seatNumber);
            seatRepository.save(seat);
        }

        return savedTrip;
    }

    /**
     * Cập nhật thông tin chuyến xe.
     */
    @Transactional
    public Trip updateTrip(Long id, TripDTO dto) {
        Trip trip = findById(id);
        trip.setRoute(routeRepository.findById(dto.getRouteId())
                .orElseThrow(() -> new RuntimeException("Tuyến đường không tồn tại")));
        Bus bus = busRepository.findById(dto.getBusId())
                .orElseThrow(() -> new RuntimeException("Xe không tồn tại"));

        trip.setBus(bus);
        trip.setDepartureTime(dto.getDepartureTime());
        trip.setArrivalTime(dto.getArrivalTime());
        trip.setTicketPrice(dto.getTicketPrice());
        trip.setStatus(dto.getStatus());

        return tripRepository.save(trip);
    }

    /**
     * Xóa/Hủy chuyến xe.
     */
    @Transactional
    public void deleteTrip(Long id) {
        Trip trip = findById(id);
        trip.setStatus(TripStatus.CANCELLED);
        tripRepository.save(trip);
    }

    private TripResultDTO toDTO(Trip trip) {
        long available = seatRepository.countByTripIdAndStatus(trip.getId(), SeatStatus.AVAILABLE);

        return TripResultDTO.builder()
                .id(trip.getId())
                .fromLocation(trip.getRoute().getFromLocation().getName())
                .toLocation(trip.getRoute().getToLocation().getName())
                .departureTime(trip.getDepartureTime())
                .arrivalTime(trip.getArrivalTime())
                .ticketPrice(trip.getTicketPrice())
                .busType(trip.getBus().getBusType().name())
                .brand(trip.getBus().getBrand())
                .driverName(trip.getBus().getDriverName())
                .plateNumber(trip.getBus().getPlateNumber())
                .distanceKm(trip.getRoute().getDistanceKm())
                .availableSeats(available)
                .build();
    }
}
