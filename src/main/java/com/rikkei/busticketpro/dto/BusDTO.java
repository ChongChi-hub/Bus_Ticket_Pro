package com.rikkei.busticketpro.dto;

import com.rikkei.busticketpro.entity.BusType;
import com.rikkei.busticketpro.entity.Status;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BusDTO {

    private Long id;

    @NotBlank(message = "Biển số xe không được để trống")
    private String plateNumber;

    @NotNull(message = "Loại xe không được để trống")
    private BusType busType;

    @NotNull(message = "Số ghế không được để trống")
    @Min(value = 1, message = "Số ghế tối thiểu là 1")
    private Integer totalSeats;

    private String brand;

    @NotBlank(message = "Tên tài xế không được để trống")
    private String driverName;

    private Status status = Status.ACTIVE;
}
