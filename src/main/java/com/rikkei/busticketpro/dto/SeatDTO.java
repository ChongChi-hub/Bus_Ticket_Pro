package com.rikkei.busticketpro.dto;

import com.rikkei.busticketpro.entity.SeatStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SeatDTO {

    private Long id;
    private String seatNumber;
    private SeatStatus status;   // AVAILABLE / PENDING / BOOKED
}
