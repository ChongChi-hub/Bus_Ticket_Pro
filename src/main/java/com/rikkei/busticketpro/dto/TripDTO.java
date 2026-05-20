package com.rikkei.busticketpro.dto;

import com.rikkei.busticketpro.entity.TripStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class TripDTO {

    private Long id;

    @NotNull(message = "Vui lòng chọn tuyến đường")
    private Long routeId;

    @NotNull(message = "Vui lòng chọn xe")
    private Long busId;

    @NotNull(message = "Vui lòng chọn thời gian khởi hành")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime departureTime;

    @NotNull(message = "Vui lòng chọn thời gian tới nơi")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime arrivalTime;

    @NotNull(message = "Vui lòng nhập giá vé")
    @Positive(message = "Giá vé phải lớn hơn 0")
    private BigDecimal ticketPrice;

    private TripStatus status = TripStatus.READY;
}
