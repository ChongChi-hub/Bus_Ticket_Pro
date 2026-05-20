package com.rikkei.busticketpro.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BookingRequestDTO {

    @NotNull(message = "Chuyến xe không hợp lệ")
    private Long tripId;

    @NotNull(message = "Ghế không hợp lệ")
    private Long seatId;

    @NotBlank(message = "Họ tên không được để trống")
    private String customerName;

    @NotBlank(message = "Số điện thoại không được để trống")
    private String phoneNumber;

    private String email;
}
