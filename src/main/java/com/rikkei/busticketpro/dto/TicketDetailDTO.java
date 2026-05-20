package com.rikkei.busticketpro.dto;

import com.rikkei.busticketpro.entity.TicketStatus;
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
public class TicketDetailDTO {

    private Long ticketId;
    private String ticketCode;
    private String customerName;
    private String phoneNumber;
    private String email;

    // Thông tin ghế
    private String seatNumber;

    // Thông tin tuyến đường
    private String fromLocation;
    private String toLocation;
    private double distanceKm;

    // Thông tin chuyến
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;

    // Thông tin xe
    private String plateNumber;
    private String busType;
    private String driverName;

    // Thông tin vé
    private BigDecimal totalAmount;
    private TicketStatus status;
    private LocalDateTime bookingTime;
    private LocalDateTime paidAt;
}
