package com.rikkei.busticketpro.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripResultDTO {

    private Long id;
    private String fromLocation;
    private String toLocation;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private BigDecimal ticketPrice;
    private String busType;
    private String brand;
    private String driverName;
    private String plateNumber;
    private long availableSeats;
    private double distanceKm;

    public String getDurationStr() {
        if (departureTime == null || arrivalTime == null) {
            return "Chưa xác định";
        }
        java.time.Duration duration = java.time.Duration.between(departureTime, arrivalTime);
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;
        return hours + " Hr " + minutes + " Min";
    }
}
